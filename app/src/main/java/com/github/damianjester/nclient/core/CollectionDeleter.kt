package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.repo.GalleryCollectionRepository

fun interface CollectionDeleter {
    suspend fun delete(id: GalleryCollectionId)
}

class DefaultCollectionDeleter(
    private val repository: GalleryCollectionRepository
) : CollectionDeleter {
    override suspend fun delete(id: GalleryCollectionId) {
        require(id.value != GalleryCollectionId.FAVORITES_ID) { "Cannot delete favorites collection." }
        repository.deleteCollection(id)
    }
}
