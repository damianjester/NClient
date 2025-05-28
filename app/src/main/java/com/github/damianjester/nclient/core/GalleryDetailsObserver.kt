package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface GalleryDetailsObserver {
    fun details(id: GalleryId): Flow<Gallery>
}

class DefaultGalleryDetailsObserver(
    private val repository: GalleryRepository
) : GalleryDetailsObserver {
    override fun details(id: GalleryId): Flow<Gallery> {
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
                        thumbnailUrl = NHentaiUrl.galleryCoverThumbnail(
                            mediaId = MediaId(entity.mediaId),
                            fileType = entity.coverThumbnailFileExtension
                                ?.let { GalleryImageFileType.fromFileExtension(it) }
                                // Cover thumbnail image file type unknown, make a best guess
                                ?: GalleryImageFileType.WEBP(hasWebpExtension = false)
                        ),
                        originalUrl = NHentaiUrl.galleryCover(
                            mediaId = MediaId(entity.mediaId),
                            fileType = entity.coverFileExtension
                                ?.let { GalleryImageFileType.fromFileExtension(it) }
                                // Cover image file type unknown, make a best guess
                                ?: GalleryImageFileType.WEBP(hasWebpExtension = false)
                        )
                    ),
                    updated = Instant.fromEpochSeconds(entity.uploadDate).toLocalDateTime(TimeZone.UTC),
                    favoriteCount = entity.numFavorites.toInt()
                )
            }
    }
}
