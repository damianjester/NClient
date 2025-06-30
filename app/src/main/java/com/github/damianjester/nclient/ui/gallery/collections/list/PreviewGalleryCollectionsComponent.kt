package com.github.damianjester.nclient.ui.gallery.collections.list

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryCollectionQuery
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.ui.preview.PreviewData

class PreviewGalleryCollectionsComponent(
    collections: List<GalleryCollection> = PreviewData.collections
) : GalleryCollectionsComponent {
    override val model: Value<GalleryCollectionsComponent.Model> =
        MutableValue(
            GalleryCollectionsComponent.Model(
                collectionsState = GalleryCollectionsComponent.CollectionsState.Loaded(
                    collections = collections
                )
            )
        )

    override fun navigateToCollection(id: GalleryCollectionId) = Unit

    override fun activateSortDialog() = Unit

    override fun activateCreateCollectionDialog() = Unit

    override fun onSortChanged(
        type: GalleryCollectionQuery.SortType,
        order: SortOrder,
    ) = Unit
}
