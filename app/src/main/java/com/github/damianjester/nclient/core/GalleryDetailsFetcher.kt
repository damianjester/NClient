package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryDetails
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.db.GalleryRepository

interface GalleryDetailsFetcher {
    suspend fun fetch(id: GalleryId): Result<GalleryDetails, NClientError>
}

class DefaultGalleryDetailsFetcher(
    private val repository: GalleryRepository,
    private val galleryDetailsCacher: GalleryDetailsCacher,
) : GalleryDetailsFetcher {
    override suspend fun fetch(id: GalleryId): Result<GalleryDetails, NClientError> {
        return when (val result = galleryDetailsCacher.cache(id)) {
            is Result.Err -> result
            is Result.Ok -> Result.Ok(repository.selectGalleryDetails(id))
        }
    }
}
