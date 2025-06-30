package com.github.damianjester.nclient.ui.gallery.random

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStart
import com.github.damianjester.nclient.core.CollectionFavoriter
import com.github.damianjester.nclient.core.RandomGalleryFetcher
import com.github.damianjester.nclient.core.models.Gallery
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.net.NHentaiClientConnectionException
import com.github.damianjester.nclient.net.NHentaiClientException
import com.github.damianjester.nclient.net.NHentaiClientScrapeException
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import com.github.damianjester.nclient.ui.gallery.random.RandomGalleryComponent.RandomGalleryState
import com.github.damianjester.nclient.ui.utils.asStateFlow
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private const val SAVED_STATE_KEY = "SAVED_STATE"

interface RandomGalleryComponent {
    val model: Value<Model>

    fun fetchNextRandom()

    fun previousGallery()

    fun favoriteGallery()

    fun navigateToGallery()

    data class Model(
        val galleyState: RandomGalleryState = RandomGalleryState.Loading,
        val randoms: List<Gallery> = emptyList(),
        val isFavorite: Boolean = false
    )

    sealed interface RandomGalleryState {
        data object Loading : RandomGalleryState

        data class Loaded(val gallery: Gallery) : RandomGalleryState

        sealed interface Error : RandomGalleryState {
            data object NetworkConnection : Error

            data object Internal : Error
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultRandomGalleryComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    private val onNavigateGallery: (GalleryId) -> Unit,
    private val fetcher: RandomGalleryFetcher,
    private val collectionRepository: GalleryCollectionRepository,
    private val favoriter: CollectionFavoriter,
) : RandomGalleryComponent, ComponentContext by componentContext {
    private var state: State = stateKeeper.consume(key = SAVED_STATE_KEY, strategy = State.serializer()) ?: State()

    private val _model = MutableValue(
        RandomGalleryComponent.Model(
            galleyState = state.randoms.lastOrNull()
                ?.let { RandomGalleryState.Loaded(it) } ?: RandomGalleryState.Loading,
            randoms = state.randoms
        )
    )
    override val model: Value<RandomGalleryComponent.Model> = _model

    private val coroutineScope = coroutineScope(dispatchers.Main.immediate)

    init {
        stateKeeper.register(key = SAVED_STATE_KEY, strategy = State.serializer()) {
            state = State(_model.value.randoms)
            state // Called when it's time to save the state
        }
        doOnStart(isOneTime = true) {
            if (_model.value.randoms.isEmpty()) {
                fetchRandom()
            }
        }
        doOnResume {
            coroutineScope.launch {
                _model.asStateFlow()
                    .mapNotNull { it.randoms.lastOrNull()?.id }
                    .distinctUntilChanged()
                    .flatMapLatest { collectionRepository.isFavorite(it) }
                    .collectLatest { isFavorite ->
                        _model.update { it.copy(isFavorite = isFavorite) }
                    }
            }
        }
    }

    override fun fetchNextRandom() {
        if (_model.value.galleyState != RandomGalleryState.Loading) {
            fetchRandom()
        }
    }

    override fun previousGallery() {
        doOnLoaded {
            _model.update {
                if (it.randoms.size > 1) {
                    val stack = ArrayDeque(it.randoms).apply { removeLast() }
                    it.copy(
                        galleyState = RandomGalleryState.Loaded(stack.last()),
                        randoms = stack.toList()
                    )
                } else {
                    it
                }
            }
        }
    }

    override fun favoriteGallery() {
        coroutineScope.launch {
            val currentGallery = _model.value.randoms.last()
            favoriter.setFavoriteState(currentGallery.id)
        }
    }

    override fun navigateToGallery() {
        doOnLoaded { loaded ->
            onNavigateGallery(loaded.gallery.id)
        }
    }

    private fun fetchRandom() {
        _model.update { it.copy(galleyState = RandomGalleryState.Loading, isFavorite = false) }

        coroutineScope.launch {
            try {
                val gallery = fetcher.fetch()
                _model.update {
                    it.copy(
                        galleyState = RandomGalleryState.Loaded(gallery),
                        randoms = it.randoms.plus(gallery)
                    )
                }
            } catch (ex: NHentaiClientException) {
                val state = when (ex) {
                    is NHentaiClientConnectionException -> RandomGalleryState.Error.NetworkConnection
                    is NHentaiClientScrapeException -> RandomGalleryState.Error.Internal
                    else -> throw ex
                }
                _model.update { it.copy(galleyState = state) }
            }
        }
    }

    private fun doOnLoaded(block: (RandomGalleryState.Loaded) -> Unit) {
        val state = _model.value.galleyState
        if (state is RandomGalleryState.Loaded) {
            block(state)
        }
    }

    @Serializable
    private class State(val randoms: List<Gallery> = emptyList())
}
