package com.github.damianjester.nclient.ui.gallery.collections.add.selection

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PreviewCollectionSelectionComponent(collections: List<GalleryCollection>) : CollectionSelectionComponent {
    override val model: Value<CollectionSelectionComponent.Model> =
        MutableValue(
            CollectionSelectionComponent.Model(
                state = CollectionSelectionComponent.CollectionsState.Loaded(collections),
                selection = listOf(1L, 2L).map { GalleryCollectionId(it) },
            )
        )

    override val submittable: Flow<Boolean> = flowOf(true)

    override fun submit() = Unit

    override fun changeSelection(id: GalleryCollectionId) = Unit

    override fun navigateCreateCollection() = Unit

    override fun onCollectionCreated(id: GalleryCollectionId) = Unit
}
