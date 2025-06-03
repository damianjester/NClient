package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryPage
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.repo.GalleryRepository

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
