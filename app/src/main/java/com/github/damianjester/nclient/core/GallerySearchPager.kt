package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.GalleryQueryEntity
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.NHentaiHttpClient

interface GallerySearchPager {
    suspend fun fetch(page: Int): Result<List<GallerySearchItem>, NClientError>
}

class DefaultGallerySearchPager(
    private val client: NHentaiHttpClient,
    private val galleryRepository: GalleryRepository,
) : GallerySearchPager {
    override suspend fun fetch(page: Int): Result<List<GallerySearchItem>, NClientError> {
        val response = client.getGalleries(page)
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
