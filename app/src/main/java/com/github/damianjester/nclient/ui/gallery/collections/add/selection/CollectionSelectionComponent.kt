package com.github.damianjester.nclient.ui.gallery.collections.add.selection

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnResume
import com.github.damianjester.nclient.core.CollectionSelectionUpdater
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryCollectionQuery
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import com.github.damianjester.nclient.ui.gallery.collections.add.AddToCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.add.DefaultAddToCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.add.selection.CollectionSelectionComponent.CollectionsState
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private const val SAVED_STATE_KEY = "SAVED_STATE"

interface CollectionSelectionComponent : AddToCollectionComponent.Child.Submittable {
    val model: Value<Model>

    fun changeSelection(id: GalleryCollectionId)

    fun navigateCreateCollection()

    fun onCollectionCreated(id: GalleryCollectionId)

    data class Model(
        val state: CollectionsState = CollectionsState.Loading,
        val selection: List<GalleryCollectionId> = emptyList(),
        val submittable: Boolean = false,
    )

    sealed interface CollectionsState {
        data object Loading : CollectionsState

        data class Loaded(val collections: List<GalleryCollection>) : CollectionsState
    }
}

class DefaultCollectionSelectionComponent(
    componentContext: ComponentContext,
    private val config: DefaultAddToCollectionComponent.Config.CollectionSelection,
    private val onNavigateCreateCollection: () -> Unit,
    private val onSubmitted: () -> Unit,
    dispatchers: NClientDispatchers,
    private val repository: GalleryCollectionRepository,
    private val selectionUpdater: CollectionSelectionUpdater
) : CollectionSelectionComponent, KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private val instance: Instance = instanceKeeper.getOrCreate {
        Instance(
            galleryId = config.id,
            state = stateKeeper.consume(SAVED_STATE_KEY, State.serializer()),
            dispatchers = get(),
            repository = get()
        )
    }

    override val submittable: Flow<Boolean> = instance.submittable

    private val _model = MutableValue(CollectionSelectionComponent.Model())
    override val model: Value<CollectionSelectionComponent.Model> = _model

    init {
        stateKeeper.register(SAVED_STATE_KEY, State.serializer()) {
            State(instance.selection.value)
        }

        doOnResume {
            coroutineScope.launch {
                launch {
                    instance.collections.collectLatest { collections ->
                        _model.update { it.copy(CollectionsState.Loaded(collections)) }
                    }
                }
                launch {
                    instance.selection.collectLatest { selection ->
                        _model.update { it.copy(selection = selection) }
                    }
                }
                launch {
                    instance.submittable.collectLatest { submittable ->
                        _model.update { it.copy(submittable = submittable) }
                    }
                }
            }
        }
    }

    override fun submit() {
        instance.coroutineScope.launch {
            selectionUpdater.update(config.id, instance.selection.value)
            onSubmitted()
        }
    }

    override fun changeSelection(id: GalleryCollectionId) {
        instance.changeSelection(id)
    }

    override fun navigateCreateCollection() = onNavigateCreateCollection()

    override fun onCollectionCreated(id: GalleryCollectionId) {
        instance.changeSelection(id)
    }

    @Serializable
    private class State(
        var selection: List<GalleryCollectionId> = emptyList(),
    )

    private class Instance(
        galleryId: GalleryId,
        state: State?,
        dispatchers: NClientDispatchers,
        repository: GalleryCollectionRepository,
    ) : InstanceKeeper.Instance {
        val coroutineScope = CoroutineScope(dispatchers.Main.immediate + SupervisorJob())

        private val query
            get() = GalleryCollectionQuery(GalleryCollectionQuery.Sort(GalleryCollectionQuery.SortType.ID, SortOrder.ASC))

        val collections = repository.getCollections(query)

        val collectionIdsOfGallery = repository.getCollectionIds(galleryId)
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        private val _selection = MutableStateFlow<List<GalleryCollectionId>>(emptyList())
        val selection: StateFlow<List<GalleryCollectionId>> = _selection

        init {
            if (state != null) {
                _selection.value = state.selection
            } else {
                coroutineScope.launch {
                    _selection.value = repository.getCollectionIds(galleryId).first()
                }
            }
        }

        val submittable: Flow<Boolean> = combine(
            selection,
            collectionIdsOfGallery,
        ) { selection, inCollectionIds ->
            val added = selection - inCollectionIds
            val removed = inCollectionIds - selection
            added.isNotEmpty() || removed.isNotEmpty()
        }

        fun changeSelection(id: GalleryCollectionId) {
            val currentSelection = _selection.value
            _selection.value = currentSelection.run {
                if (currentSelection.contains(id)) {
                    minus(id)
                } else {
                    plus(id)
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            coroutineScope.cancel()
        }
    }
}
