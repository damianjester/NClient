package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import kotlinx.datetime.Clock

fun interface CollectionGalleryRemover {
    suspend fun remove(galleryId: GalleryId, collectionId: GalleryCollectionId)
}

class DefaultCollectionGalleryRemover(
    private val repository: GalleryCollectionRepository
) : CollectionGalleryRemover {
    override suspend fun remove(galleryId: GalleryId, collectionId: GalleryCollectionId) {
        repository.removeGallery(galleryId, listOf(collectionId), Clock.System.now())
    }
}
