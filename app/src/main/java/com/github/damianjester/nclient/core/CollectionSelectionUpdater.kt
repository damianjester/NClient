package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

fun interface CollectionSelectionUpdater {
    suspend fun update(
        id: GalleryId,
        selection: List<GalleryCollectionId>,
    )
}

class DefaultCollectionSelectionUpdater(
    private val repository: GalleryCollectionRepository,
) : CollectionSelectionUpdater {
    override suspend fun update(
        id: GalleryId,
        selection: List<GalleryCollectionId>,
    ) {
        val currentCollectionIds = repository.getCollectionIds(id).first()

        val added = selection - currentCollectionIds
        val removed = currentCollectionIds - selection

        require(added.isNotEmpty() || removed.isNotEmpty()) {
            "Selection did not change for gallery $id."
        }

        repository.addAndRemoveGallery(
            galleryId = id,
            added = added,
            removed = removed,
            timestamp = Clock.System.now()
        )
    }
}
