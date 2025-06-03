package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.GalleryRepository

fun interface GalleryPagesFetcher {
    suspend fun fetch(id: GalleryId): Result<List<GalleryPage>, NClientError>
}

class DefaultGalleryPagesFetcher(
    private val repository: GalleryRepository,
    private val galleryDetailsCacher: GalleryDetailsCacher,
) : GalleryPagesFetcher {
    override suspend fun fetch(id: GalleryId): Result<List<GalleryPage>, NClientError> {
        return when (val result = galleryDetailsCacher.cache(id)) {
            is Result.Err -> result
            is Result.Ok -> Result.Ok(repository.selectGalleryPages(id))
        }
    }
}
