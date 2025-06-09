package com.github.damianjester.nclient.ui.gallery.history

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.getAndUpdate
import com.arkivanov.decompose.value.update
import com.arkivanov.decompose.value.updateAndGet
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnStart
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery.SortType
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.repo.GalleryHistoryRepository
import com.github.damianjester.nclient.ui.gallery.history.HistoryComponent.GalleriesState
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private const val SAVED_STATE_KEY = "SAVED_STATE"
private const val PAGE_LIMIT = 20

interface HistoryComponent {
    val model: Value<Model>

    fun changeSort(type: SortType, order: SortOrder)

    fun clearHistory()

    fun loadNextPage()

    fun navigateToGallery(id: GalleryId)

    data class Model(
        val galleriesState: GalleriesState = GalleriesState.Loading,
        val sort: GalleryHistoryQuery.Sort = GalleryHistoryQuery.Sort(SortType.LastVisit),
    )

    sealed interface GalleriesState {
        data object Loading : GalleriesState

        data class Loaded(
            val galleries: List<GallerySummary>,
            val loadingNextPage: Boolean,
        ) : GalleriesState
    }
}

class DefaultHistoryComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    private val onNavigateGallery: (GalleryId) -> Unit,
    private val historyRepository: GalleryHistoryRepository,
) : HistoryComponent, ComponentContext by componentContext {
    private var savedState = stateKeeper.consume(SAVED_STATE_KEY, SavedState.serializer()) ?: SavedState()
    private val historyInstance = instanceKeeper.getOrCreate { HistoryInstance(savedState.sort) }
    private val _model = MutableValue(HistoryComponent.Model(
        galleriesState = historyInstance.state.value.galleries.takeIf { it.isNotEmpty() }
            ?.let { GalleriesState.Loaded(it, false) }
            ?: GalleriesState.Loading,
        sort = historyInstance.state.value.sort
    ))
    override val model: Value<HistoryComponent.Model> = _model

    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    init {
        stateKeeper.register(SAVED_STATE_KEY, SavedState.serializer()) {
            savedState = SavedState(model.value.sort)
            savedState
        }
        doOnStart(isOneTime = true) {
            if (historyInstance.state.value.galleries.isEmpty()) {
                coroutineScope.launch {
                    loadPage(model.value.sort)
                }
            }
        }
    }

    override fun changeSort(type: SortType, order: SortOrder) {
        doOnLoaded {
            coroutineScope.launch {
                val sort = historyInstance.state
                    .updateAndGet { HistoryInstance.State(sort = GalleryHistoryQuery.Sort(type, order)) }
                    .sort

                _model.update { it.copy(sort = sort) }
                loadPage(sort)
            }
        }
    }

    override fun clearHistory() {
        doOnLoaded {
            _model.update { it.copy(galleriesState = GalleriesState.Loading) }
            coroutineScope.launch {
                historyRepository.deleteAll()
                historyInstance.state.update { it.copy(galleries = emptyList(), pageOffset = 0, endOfResults = false) }
                _model.update { it.copy(galleriesState = GalleriesState.Loaded(emptyList(), false)) }
            }
        }
    }

    override fun loadNextPage() {
        if (model.value.galleriesState is GalleriesState.Loading || historyInstance.state.value.endOfResults) {
            return
        }

        doOnLoaded { loaded ->
            coroutineScope.launch {
                loadPage(model.value.sort, loaded)
            }
        }
    }

    override fun navigateToGallery(id: GalleryId) = onNavigateGallery(id)

    private suspend fun loadPage(sort: GalleryHistoryQuery.Sort, loaded: GalleriesState.Loaded? = null) {
        _model.update {
            it.copy(
                galleriesState = loaded?.copy(loaded.galleries, loadingNextPage = true) ?: GalleriesState.Loading
            )
        }

        val pageOffset = historyInstance.state.getAndUpdate { it.copy(pageOffset = it.pageOffset + 1) }.pageOffset

        val query = GalleryHistoryQuery(
            limit = PAGE_LIMIT,
            pageOffset = pageOffset,
            sort = sort
        )

        val newGalleries = historyRepository.getVisits(query)
            .map { it.gallery }

        val galleries = historyInstance.state.updateAndGet {
            val galleries = loaded?.galleries?.plus(newGalleries) ?: newGalleries
            it.copy(galleries = galleries, endOfResults = newGalleries.size < PAGE_LIMIT)
        }.galleries

        _model.update {
            it.copy(galleriesState = GalleriesState.Loaded(galleries, false))
        }
    }

    private fun doOnLoaded(block: (GalleriesState.Loaded) -> Unit) {
        val state = _model.value
        if (state.galleriesState is GalleriesState.Loaded) {
            block(state.galleriesState)
        }
    }

    private class HistoryInstance(sort: GalleryHistoryQuery.Sort) : InstanceKeeper.Instance {
        val state = MutableValue(State(sort))

        override fun onDestroy() = Unit

        data class State(
            val sort: GalleryHistoryQuery.Sort,
            val galleries: List<GallerySummary> = emptyList(),
            val pageOffset: Int = 0,
            val endOfResults: Boolean = false,
        )
    }

    @Serializable
    private class SavedState(
        val sort: GalleryHistoryQuery.Sort = GalleryHistoryQuery.Sort(SortType.LastVisit)
    )
}
