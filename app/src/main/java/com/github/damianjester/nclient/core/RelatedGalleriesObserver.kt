package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface RelatedGalleriesObserver {
    fun galleries(id: GalleryId): Flow<List<RelatedGallery>>
}

class DefaultRelatedGalleriesObserver(
    private val galleryRepository: GalleryRepository,
) : RelatedGalleriesObserver {
    override fun galleries(id: GalleryId): Flow<List<RelatedGallery>> {
        return galleryRepository.selectRelatedGalleries(id)
            .map { related ->
                related.map { gal ->
                    RelatedGallery(
                        id = GalleryId(gal.id),
                        title = gal.title,
                        language = GalleryLanguage.fromLongTagId(gal.tagIds),
                        image = GalleryImage.Remote(
                            NHentaiUrl.galleryCoverThumbnail(
                                MediaId(gal.mediaId),
                                gal.coverThumbnailFileExtension
                                    ?.let { GalleryImageFileType.fromFileExtension(it) }
                                    // Cover thumbnail image file type unknown, make a best guess
                                    ?: GalleryImageFileType.WEBP(hasWebpExtension = false)
                            )
                        )
                    )
                }
            }
    }
}
