package com.github.damianjester.nclient.ui.gallery.collections.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnResume
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryCollectionQuery
import com.github.damianjester.nclient.core.models.GalleryCollectionQuery.SortType
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import com.github.damianjester.nclient.ui.gallery.collections.list.GalleryCollectionsComponent.CollectionsState
import com.github.damianjester.nclient.ui.sort.SortChangeListener
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

const val SAVED_STATE_KEY = "SAVED_STATE"

interface GalleryCollectionsComponent : SortChangeListener<SortType> {
    val model: Value<Model>

    fun navigateToCollection(id: GalleryCollectionId)

    fun activateSortDialog()

    fun activateCreateCollectionDialog()

    data class Model(
        val collectionsState: CollectionsState = CollectionsState.Loading,
    )

    sealed interface CollectionsState {
        data object Loading : CollectionsState

        data class Loaded(
            val collections: List<GalleryCollection> = emptyList(),
        ) : CollectionsState
    }
}

class DefaultGalleryCollectionsComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    private val onNavigateCollection: (GalleryCollectionId) -> Unit,
    private val onActivateSortDialog: (GalleryCollectionQuery.Sort) -> Unit,
    private val onActivateCreateCollectionDialog: () -> Unit,
    private val repository: GalleryCollectionRepository,
) : GalleryCollectionsComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private var state: State = stateKeeper.consume(key = SAVED_STATE_KEY, strategy = State.serializer()) ?: State()
    private val instance = instanceKeeper.getOrCreate { Instance(state, coroutineScope, repository) }

    private val _model = MutableValue(GalleryCollectionsComponent.Model())
    override val model: Value<GalleryCollectionsComponent.Model> = _model

    init {
        stateKeeper.register(key = SAVED_STATE_KEY, strategy = State.serializer()) {
            state
        }
        doOnResume {
            coroutineScope.launch {
                instance.collections.collectLatest { collections ->
                    _model.update { it.copy(CollectionsState.Loaded(collections)) }
                }
            }
        }
    }

    override fun navigateToCollection(id: GalleryCollectionId) = onNavigateCollection(id)

    override fun onSortChanged(type: SortType, order: SortOrder) {
        doOnLoaded {
            state = state.copy(query = state.query.copy(sort = GalleryCollectionQuery.Sort(type, order)))
            instance.changeQuery(state.query)
        }
    }

    override fun activateSortDialog() {
        doOnLoaded {
            onActivateSortDialog(state.query.sort)
        }
    }

    override fun activateCreateCollectionDialog() = onActivateCreateCollectionDialog()

    private fun doOnLoaded(block: (CollectionsState.Loaded) -> Unit) {
        val state = _model.value.collectionsState
        if (state is CollectionsState.Loaded) {
            block(state)
        }
    }

    @Serializable
    private data class State(
        val query: GalleryCollectionQuery = GalleryCollectionQuery.Default,
    )

    private class Instance(
        savedState: State?,
        coroutineScope: CoroutineScope,
        private val repository: GalleryCollectionRepository,
    ) : InstanceKeeper.Instance {
        private val query: MutableStateFlow<GalleryCollectionQuery> =
            MutableStateFlow(savedState?.query ?: GalleryCollectionQuery.Default)

        @OptIn(ExperimentalCoroutinesApi::class)
        val collections: StateFlow<List<GalleryCollection>> =
            query.flatMapLatest { query -> repository.getCollections(query) }
                .stateIn(
                    coroutineScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )

        fun changeQuery(query: GalleryCollectionQuery) {
            this.query.value = query
        }
    }
}
