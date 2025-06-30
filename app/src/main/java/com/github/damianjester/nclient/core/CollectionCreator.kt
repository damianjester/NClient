package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import kotlinx.datetime.Clock

fun interface CollectionCreator {
    suspend fun create(name: String): GalleryCollectionId
}

class DefaultCollectionCreator(
    private val repository: GalleryCollectionRepository,
) : CollectionCreator {
    override suspend fun create(name: String): GalleryCollectionId {
        require(CollectionValidation.validateName(name)) { "Collection name must be valid." }
        return repository.createCollection(name, Clock.System.now())
    }
}
