package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.GalleryQueryEntity
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.NHentaiHttpClient

interface GallerySearchPager {
    suspend fun fetch(page: Int): Result<List<GallerySummary>, NClientError>
}

class DefaultGallerySearchPager(
    private val client: NHentaiHttpClient,
    private val galleryRepository: GalleryRepository,
) : GallerySearchPager {
    override suspend fun fetch(page: Int): Result<List<GallerySummary>, NClientError> {
        val response = client.getGallerySummaries(page)
        galleryRepository.replaceAllGallerySummaries(defaultHomeQuery, response)

        val galleries = galleryRepository.selectSummariesForQuery(defaultHomeQuery)

        return Result.Ok(galleries)
    }

    companion object {
        val defaultHomeQuery = GalleryQueryEntity(
            id = 1,
            searchQuery = null,
        )
    }
}
