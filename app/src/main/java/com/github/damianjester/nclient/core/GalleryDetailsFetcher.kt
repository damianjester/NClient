package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.GalleryDetailsEntity
import com.github.damianjester.nclient.GalleryPageEntity
import com.github.damianjester.nclient.GallerySummaryEntity
import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.core.GalleryDetailsFetcher.Result
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.GalleryResponse
import com.github.damianjester.nclient.net.NHentaiHttpClient
import com.github.damianjester.nclient.net.NHentaiUrl.lastSegmentFileExtension
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.withContext

interface GalleryDetailsFetcher {
    suspend fun fetch(id: GalleryId): Result

    sealed interface Result {
        data object Success : Result

        data class Failure(val exception: Exception) : Result
    }
}

class DefaultGalleryDetailsFetcher(
    private val client: NHentaiHttpClient,
    private val repository: GalleryRepository,
    private val dispatchers: NClientDispatchers,
    private val logger: Logger,
) : GalleryDetailsFetcher {
    override suspend fun fetch(id: GalleryId) = withContext(dispatchers.IO) {
        val response: GalleryResponse

        try {
            response = client.getGallery(id)
        } catch (ex: Exception) {
            logger.e(LogTags.gallery, "Failed to fetch details for gallery $id.", ex)
            return@withContext Result.Failure(ex)
        }

        val summary = GallerySummaryEntity(
            id = response.gallery.id.value,
            mediaId = response.gallery.mediaId,
            prettyTitle = response.gallery.title.pretty,
            coverThumbnailFileExtension = response.coverUrl?.lastSegmentFileExtension
                ?: GalleryImageFileType
                    .fromType(response.gallery.images.thumbnail.t)
                    .toFileExtension()
        )

        val details = mapDetails(id, response)
        val pages = mapPages(id, response)
        val tags = mapTags(response)
        val related = mapRelated(response)

        try {
            repository.insertGalleryWithDetails(summary, details, pages, tags, related)
        } catch (ex: Exception) {
            logger.e(LogTags.gallery, "Failed to insert details for gallery $id.", ex)
            return@withContext Result.Failure(ex)
        }

        Result.Success
    }
}

private fun mapDetails(id: GalleryId, response: GalleryResponse) =
    GalleryDetailsEntity(
        galleryId = id.value,
        coverFileExtension = response.coverUrl?.lastSegmentFileExtension
            ?: GalleryImageFileType
                .fromType(response.gallery.images.cover.t)
                .toFileExtension(),
        numFavorites = response.gallery.numFavorites.toLong(),
        englishTitle = response.gallery.title.english,
        japaneseTitle = response.gallery.title.japanese,
        uploadDate = response.gallery.uploadDate
    )

private fun mapPages(id: GalleryId, response: GalleryResponse) =
    response.gallery.images.pages
        .mapIndexed { i, p ->
            GalleryPageEntity(
                galleryId = id.value,
                pageIndex = i.toLong(),
                fileExtension = GalleryImageFileType.fromType(p.t).toFileExtension(),
                width = p.w.toLong(),
                height = p.h.toLong()
            )
        }

private fun mapTags(response: GalleryResponse) =
    response.gallery.tags
        .map { t ->
            TagEntity(
                id = t.id.value,
                type = t.type,
                name = t.name,
                count = t.count.toLong(),
                urlPath = t.url
            )
        }

private fun mapRelated(response: GalleryResponse) =
    response.related.map { gal ->

        val gallery = GallerySummaryEntity(
            id = gal.id.value,
            prettyTitle = gal.title,
            mediaId = gal.mediaId,
            coverThumbnailFileExtension = gal.coverThumbnailUrl.lastSegmentFileExtension,
        )

        gallery to gal.tagIds
    }
