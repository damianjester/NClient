package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.repo.GalleryCollectionRepository

fun interface CollectionRenamer {
    suspend fun rename(id: GalleryCollectionId, name: String)
}

class DefaultCollectionRenamer(
    private val repository: GalleryCollectionRepository
) : CollectionRenamer {
    override suspend fun rename(id: GalleryCollectionId, name: String) {
        require(CollectionValidation.validateName(name)) { "Collection name must be valid." }
        repository.rename(id, name)
    }
}
