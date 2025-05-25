package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.Gallery
import com.github.damianjester.nclient.GalleryCover
import com.github.damianjester.nclient.GalleryId
import com.github.damianjester.nclient.GalleryPageImageFileType
import com.github.damianjester.nclient.GalleryTitle
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.GalleryResponse
import com.github.damianjester.nclient.net.NHentaiHttpClient
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface GalleryFetcher {
    fun fetch(id: GalleryId): Flow<Gallery>
}

class DefaultGalleryFetcher(
    private val repository: GalleryRepository
) : GalleryFetcher {

    override fun fetch(id: GalleryId): Flow<Gallery> {
        return repository.selectGalleryDetails(id)
            .map { entity ->
                Gallery(
                    id = GalleryId(entity.id),
                    title = GalleryTitle(
                        english = entity.englishTitle,
                        japanese = entity.japaneseTitle,
                        pretty = entity.prettyTitle
                    ),
                    cover = GalleryCover(
                        // TODO: Move to object
                        // TODO: Can hardcode .webp file extension?
                        thumbnailUrl = Url("https://t1.nhentai.net/galleries/${entity.mediaId}/thumb.webp"),
                        originalUrl = Url("https://t1.nhentai.net/galleries/${entity.mediaId}/cover.webp"),
                    ),
                    updated = Instant.fromEpochSeconds(entity.uploadDate).toLocalDateTime(TimeZone.UTC),
                    favoriteCount = entity.numFavorites.toInt()
                )
            }
    }

}
