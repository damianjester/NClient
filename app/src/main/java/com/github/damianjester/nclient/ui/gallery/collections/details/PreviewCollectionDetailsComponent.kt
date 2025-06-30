package com.github.damianjester.nclient.ui.gallery.collections.details

import androidx.paging.PagingData
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.models.CollectionDetailsQuery
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.ui.gallery.collections.details.topbar.TopAppBarComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PreviewCollectionDetailsComponent(
    summaries: List<GallerySummary>,
    isFavorite: Boolean = false
) : CollectionDetailsComponent {
    override val topAppBar: TopAppBarComponent = object : TopAppBarComponent {
        override val model: Value<TopAppBarComponent.Model> = MutableValue(
            TopAppBarComponent.Model(
                collectionName = "Keystrokes",
                isFavorite = isFavorite
            )
        )

        override fun navigateBack() = Unit

        override fun activateSortDialog() = Unit

        override fun activateRenameDialog() = Unit

        override fun activateDeleteDialog() = Unit
    }

    override val pagingData: Flow<PagingData<GallerySummary>> =
        MutableStateFlow(PagingData.from(summaries))

    override fun navigateToGallery(id: GalleryId) = Unit

    override fun activateAddToCollectionDialog(id: GalleryId) = Unit

    override fun removeFromCollection(id: GalleryId) = Unit

    override fun onSortChanged(
        type: CollectionDetailsQuery.SortType,
        order: SortOrder,
    ) = Unit
}
