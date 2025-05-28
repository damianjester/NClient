package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.GalleryQueryEntity
import com.github.damianjester.nclient.GallerySummaryEntity
import com.github.damianjester.nclient.core.GallerySearchPager.Result
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.GalleriesResponse
import com.github.damianjester.nclient.net.NHentaiHttpClient
import com.github.damianjester.nclient.net.NHentaiUrl.lastSegmentFileExtension
import com.github.damianjester.nclient.utils.LogTags
import com.github.damianjester.nclient.utils.Logger
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.withContext

interface GallerySearchPager {
    suspend fun fetch(page: Int): Result

    sealed interface Result {
        data object Success : Result

        data class Failure(val exception: Exception) : Result
    }
}

class DefaultGallerySearchPager(
    private val logger: Logger,
    private val dispatchers: NClientDispatchers,
    private val client: NHentaiHttpClient,
    private val galleryRepository: GalleryRepository
) : GallerySearchPager {
    override suspend fun fetch(page: Int): Result = withContext(dispatchers.IO) {
        val response: GalleriesResponse

        try {
            response = client.getGalleries(page)
        } catch (ex: Exception) {
            logger.e(LogTags.gallery, "Failed to fetch galleries.", ex)
            return@withContext Result.Failure(ex)
        }

        val galleryEntities = response.galleries
            .mapIndexed { i, gal ->
                GallerySummaryEntity(
                    id = gal.id.value,
                    prettyTitle = gal.title,
                    mediaId = gal.mediaId,
                    coverThumbnailFileExtension = gal.coverThumbnailUrl.lastSegmentFileExtension,
                )
            }

        val galleryHasTag = galleryEntities.associateWith { gal ->
            response.galleries.first { it.id.value == gal.id }.tagIds
        }

        try {
            galleryRepository.insertGallerySummaries(
                defaultHomeQuery,
                galleryEntities,
                galleryHasTag
            )
        } catch (ex: Exception) {
            logger.e(LogTags.gallery, "Failed to insert galleries.", ex)
            return@withContext Result.Failure(ex)
        }

        Result.Success
    }

    companion object {
        val defaultHomeQuery = GalleryQueryEntity(
            id = 1,
            searchQuery = null,
        )
    }
}
