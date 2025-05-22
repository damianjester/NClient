package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.GalleriesResponse
import com.github.damianjester.nclient.GalleryEntity
import com.github.damianjester.nclient.NClientDispatchers
import com.github.damianjester.nclient.NHentaiHttpClient
import com.github.damianjester.nclient.core.GalleryPager.*
import com.github.damianjester.nclient.db.GalleryEntityRepository
import kotlinx.coroutines.withContext

interface GalleryPager {
    suspend fun load(page: Int): Result

    sealed interface Result {
        data object Success : Result
        data class Failure(val exception: Exception) : Result
    }
}

class DefaultGalleryPager(
    private val client: NHentaiHttpClient,
    private val dispatchers: NClientDispatchers,
    private val galleryEntityRepository: GalleryEntityRepository
) : GalleryPager {
    override suspend fun load(page: Int): Result = withContext(dispatchers.IO) {

        val response: GalleriesResponse

        try {
            response = client.getGalleries(1)
        } catch (ex: Exception) {
            return@withContext Result.Failure(ex)
        }

        val galleryEntities = response.galleries
            .mapIndexed { i, gal ->
                GalleryEntity(
                    id = gal.id.value,
                    title = gal.title,
                    mediaId = gal.mediaId,
                    coverThumbnailUrl = gal.coverThumbnailUrl.toString(),
                    page = page.toLong(),
                    orderIndex = i.toLong()
                )
            }

        val galleryHasTag = galleryEntities
            .associate { gal -> gal to response.galleries.first { it.id.value == gal.id }.tagIds }

        try {
            galleryEntityRepository.insertAll(galleryEntities, galleryHasTag)
        } catch (ex: Exception) {
            return@withContext Result.Failure(ex)
        }

        Result.Success
    }
}
