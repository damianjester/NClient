package com.github.damianjester.nclient.ui.gallery.history

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery.SortType
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.repo.GalleryHistoryRepository
import com.github.damianjester.nclient.ui.sort.SortChangeListener
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private const val SAVED_STATE_KEY = "SAVED_STATE"

interface HistoryComponent : SortChangeListener<SortType> {
    val galleries: Flow<PagingData<GallerySummary>>

    fun clearHistory()

    fun navigateToGallery(id: GalleryId)

    fun activateSortDialog()
}

class DefaultHistoryComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    private val onNavigateGallery: (GalleryId) -> Unit,
    private val onActivateSortDialog: (sort: GalleryHistoryQuery.Sort) -> Unit,
    private val historyRepository: GalleryHistoryRepository,
) : HistoryComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private var state = stateKeeper.consume(SAVED_STATE_KEY, State.serializer()) ?: State()
    private val instance = instanceKeeper.getOrCreate {
        HistoryInstance(
            savedState = state,
            dispatchers = dispatchers,
            repository = historyRepository,
        )
    }

    override val galleries: Flow<PagingData<GallerySummary>> = instance.galleries

    init {
        stateKeeper.register(SAVED_STATE_KEY, State.serializer()) {
            state = State(instance.query.value.sort)
            state
        }
    }

    override fun clearHistory() {
        coroutineScope.launch {
            historyRepository.deleteAll()
        }
    }

    override fun navigateToGallery(id: GalleryId) = onNavigateGallery(id)

    override fun activateSortDialog() = onActivateSortDialog(instance.query.value.sort)

    override fun onSortChanged(type: SortType, order: SortOrder) {
        instance.changeSort(GalleryHistoryQuery.Sort(type, order))
    }

    @Serializable
    private class State(
        val sort: GalleryHistoryQuery.Sort = GalleryHistoryQuery.Sort(SortType.LastVisit),
    )

    private class HistoryInstance(
        savedState: State,
        dispatchers: NClientDispatchers,
        repository: GalleryHistoryRepository,
    ) : InstanceKeeper.Instance {
        private val coroutineScope = CoroutineScope(dispatchers.Main.immediate + SupervisorJob())

        private val _query = MutableStateFlow(GalleryHistoryQuery(savedState.sort))
        val query: StateFlow<GalleryHistoryQuery> = _query

        @OptIn(ExperimentalCoroutinesApi::class)
        val galleries = query.flatMapLatest { query ->
            Pager(PagingConfig(pageSize = 20)) {
                repository.getVisits(query)
            }.flow.map { data -> data.map { it.gallery } }
        }.cachedIn(coroutineScope)

        fun changeSort(sort: GalleryHistoryQuery.Sort) {
            _query.value = query.value.copy(sort = sort)
        }

        override fun onDestroy() {
            coroutineScope.cancel()
        }
    }
}
