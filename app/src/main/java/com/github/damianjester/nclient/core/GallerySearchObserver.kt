package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.GalleryQueryEntity
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GallerySearchObserver {
    fun galleries(): Flow<List<GallerySearchItem>>
}

class DefaultGallerySearchObserver(
    private val galleryRepository: GalleryRepository,
) : GallerySearchObserver {
    override fun galleries(): Flow<List<GallerySearchItem>> {
        return galleryRepository
            .selectSummariesForQuery(GalleryQueryEntity(id = 1, searchQuery = null))
            .map { galleries ->
                galleries.map { gal ->
                    GallerySearchItem(
                        id = GalleryId(gal.id),
                        title = gal.title,
                        language = GalleryLanguage.fromLongTagId(gal.tagIds),
                        images = GallerySearchItemImages.Remote(
                            thumbnail = GalleryImage.Remote(
                                NHentaiUrl.galleryCoverThumbnail(
                                    mediaId = MediaId(gal.mediaId),
                                    fileType = gal.coverThumbnailFileExtension
                                        ?.let { GalleryImageFileType.fromFileExtension(it) }
                                        ?: GalleryImageFileType.WEBP(hasWebpExtension = false)
                                )
                            )
                        )
                    )
                }
            }
    }
}
