package com.github.damianjester.nclient.ui.gallery.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.github.damianjester.nclient.core.GallerySearchPager
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.net.NHentaiClientConnectionException
import com.github.damianjester.nclient.net.NHentaiClientException
import com.github.damianjester.nclient.net.NHentaiClientScrapeException
import com.github.damianjester.nclient.ui.gallery.search.GallerySearchComponent.GalleriesState
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface GallerySearchComponent {
    val model: Value<Model>

    fun navigateToGallery(gallery: GallerySummary)

    data class Model(
        val galleriesState: GalleriesState = GalleriesState.Loading,
    )

    sealed interface GalleriesState {
        data object Loading : GalleriesState

        data class Loaded(
            val galleries: List<GallerySummary> = emptyList(),
        ) : GalleriesState

        sealed interface Error : GalleriesState {
            data object NetworkConnection : Error

            data object Internal : Error
        }
    }
}

class DefaultGallerySearchComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    private val pager: GallerySearchPager,
    val onNavigateGallery: (GalleryId) -> Unit,
) : GallerySearchComponent, ComponentContext by componentContext, KoinComponent {
    private val _model = MutableValue(GallerySearchComponent.Model())
    override val model: Value<GallerySearchComponent.Model> = _model

    private val coroutineScope = coroutineScope(dispatchers.Main.immediate)

    init {
        lifecycle.doOnCreate {
            // TODO: Preload tags? Refresh tags?
            coroutineScope.launch {
                fetch()
            }
        }
    }

    private suspend fun fetch() {
        try {
            _model.update { state -> state.copy(galleriesState = GalleriesState.Loading) }
            val targetState = when (val result = pager.fetch(1)) {
                is Result.Err -> error(result.cause)
                is Result.Ok -> GalleriesState.Loaded(result.value)
            }
            _model.update { state -> state.copy(galleriesState = targetState) }
        } catch (ex: NHentaiClientException) {
            val targetState = when (ex) {
                is NHentaiClientConnectionException -> GalleriesState.Error.NetworkConnection
                is NHentaiClientScrapeException -> GalleriesState.Error.Internal
                else -> throw ex
            }
            _model.update { state -> state.copy(galleriesState = targetState) }
        }
    }

    override fun navigateToGallery(gallery: GallerySummary) {
        onNavigateGallery(gallery.id)
    }
}
