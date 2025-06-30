package com.github.damianjester.nclient.ui.gallery.collections.details

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.github.damianjester.nclient.core.CollectionGalleryRemover
import com.github.damianjester.nclient.core.models.CollectionDetailsQuery
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import com.github.damianjester.nclient.ui.DefaultRootComponent
import com.github.damianjester.nclient.ui.gallery.collections.details.topbar.DefaultTopAppBarComponent
import com.github.damianjester.nclient.ui.gallery.collections.details.topbar.OnActivateDeleteDialog
import com.github.damianjester.nclient.ui.gallery.collections.details.topbar.OnActivateRenameDialog
import com.github.damianjester.nclient.ui.gallery.collections.details.topbar.TopAppBarComponent
import com.github.damianjester.nclient.ui.sort.SortChangeListener
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

const val SAVED_STATE_KEY = "SAVED_STATE"

interface CollectionDetailsComponent : SortChangeListener<CollectionDetailsQuery.SortType> {
    val topAppBar: TopAppBarComponent

    val pagingData: Flow<PagingData<GallerySummary>>

    fun navigateToGallery(id: GalleryId)

    fun activateAddToCollectionDialog(id: GalleryId)

    fun removeFromCollection(id: GalleryId)
}

class DefaultCollectionDetailsComponent(
    componentContext: ComponentContext,
    private val config: DefaultRootComponent.Config.CollectionDetails,
    private val onNavigateGallery: (GalleryId) -> Unit,
    private val onNavigateBack: () -> Unit,
    private val onActivateSortDialog: (CollectionDetailsQuery.Sort) -> Unit,
    private val onActivateRenameDialog: OnActivateRenameDialog,
    private val onActivateDeleteDialog: OnActivateDeleteDialog,
    private val onActivateAddToCollectionDialog: (GalleryId) -> Unit,
    dispatchers: NClientDispatchers,
    private val repository: GalleryCollectionRepository,
    private val collectionGalleryRemover: CollectionGalleryRemover,
) : CollectionDetailsComponent, ComponentContext by componentContext, KoinComponent {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private var state = stateKeeper.consume(SAVED_STATE_KEY, State.serializer()) ?: State(config.id)
    private val instance = instanceKeeper.getOrCreate { Instance(state, coroutineScope, repository) }
    override val pagingData = instance.pagingData

    override val topAppBar: TopAppBarComponent =
        DefaultTopAppBarComponent(
            context = childContext("TopAppBar"),
            collectionId = config.id,
            onNavigateBack = onNavigateBack,
            onActivateSortDialog = { onActivateSortDialog(state.query.sort) },
            onActivateRenameDialog = onActivateRenameDialog,
            onActivateDeleteDialog = onActivateDeleteDialog,
            dispatchers = get(),
            repository = get()
        )

    init {
        stateKeeper.register(key = SAVED_STATE_KEY, strategy = State.serializer()) {
            state
        }
    }

    override fun navigateToGallery(id: GalleryId) = onNavigateGallery(id)

    override fun activateAddToCollectionDialog(id: GalleryId) = onActivateAddToCollectionDialog(id)

    override fun removeFromCollection(id: GalleryId) {
        coroutineScope.launch {
            collectionGalleryRemover.remove(galleryId = id, collectionId = config.id)
        }
    }

    override fun onSortChanged(type: CollectionDetailsQuery.SortType, order: SortOrder) {
        state = state.copy(query = state.query.copy(sort = CollectionDetailsQuery.Sort(type, order)))
        instance.changeSort(state.query.sort)
    }

    @Serializable
    private data class State(val query: CollectionDetailsQuery) {
        constructor(id: GalleryCollectionId) : this(CollectionDetailsQuery(id))
    }

    private class Instance(
        savedState: State,
        coroutineScope: CoroutineScope,
        private val repository: GalleryCollectionRepository,
    ) : InstanceKeeper.Instance {
        private val query = MutableStateFlow(savedState.query)

        @OptIn(ExperimentalCoroutinesApi::class)
        val pagingData = query.flatMapLatest { query ->
            Pager(PagingConfig(pageSize = 20)) {
                repository.getGalleries(query)
            }.flow
        }.cachedIn(coroutineScope)

        fun changeSort(sort: CollectionDetailsQuery.Sort) {
            query.value = query.value.copy(sort = sort)
        }
    }
}
