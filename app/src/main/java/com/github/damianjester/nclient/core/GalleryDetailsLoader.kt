package com.github.damianjester.nclient.core

import android.util.Log
import com.github.damianjester.nclient.GalleryDetailsEntity
import com.github.damianjester.nclient.GalleryId
import com.github.damianjester.nclient.GalleryPageEntity
import com.github.damianjester.nclient.GalleryPageImageFileType
import com.github.damianjester.nclient.NClientDispatchers
import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.core.GalleryDetailsLoader.Result
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.GalleryResponse
import com.github.damianjester.nclient.net.NHentaiHttpClient
import kotlinx.coroutines.withContext


interface GalleryDetailsLoader {
    suspend fun load(id: GalleryId): Result

    sealed interface Result {
        data object Success : Result
        data class Failure(val exception: Exception) : Result
    }
}

class DefaultGalleryDetailsLoader(
    private val client: NHentaiHttpClient,
    private val repository: GalleryRepository,
    private val dispatchers: NClientDispatchers,
) : GalleryDetailsLoader {

    override suspend fun load(id: GalleryId) = withContext(dispatchers.IO) {

        val response: GalleryResponse

        try {
            response = client.getGallery(id)
            Log.wtf("loader", "response: $response")
        } catch (ex: Exception) {
            Log.wtf("loader", "response: $ex")
            return@withContext Result.Failure(ex)
        }

        val details = mapDetails(id, response)
        val pages = mapPages(id, response)
        val tags = mapTags(response)

        try {
            repository.insertDetails(details, pages, tags)
        } catch (ex: Exception) {
            return@withContext Result.Failure(ex)
        }

        Result.Success
    }

}

private fun mapDetails(id: GalleryId, response: GalleryResponse) =
    GalleryDetailsEntity(
        galleryId = id.value,
        numFavorites = response.gallery.numFavorites.toLong(),
        prettyTitle = response.gallery.title.pretty,
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
                fileExtension = GalleryPageImageFileType.fromType(p.t).toThumbnailFileExtension(),
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

