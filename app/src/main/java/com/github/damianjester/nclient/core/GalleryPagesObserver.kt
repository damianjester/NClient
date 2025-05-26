package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

interface GalleryPagesObserver {
    fun pages(id: GalleryId): Flow<List<GalleryPage>>
}

class DefaultGalleryPagesObserver(
    private val galleryRepository: GalleryRepository,
) : GalleryPagesObserver {
    override fun pages(id: GalleryId): Flow<List<GalleryPage>> {
        return combine(
            flow<MediaId> { emit(galleryRepository.selectMediaIdForGallery(id)) },
            galleryRepository.selectPagesForGallery(id)
        ) { mediaId, pages ->
            pages.map { page ->

                val fileExtension = GalleryPageImageFileType.fromType(page.fileExtension)

                GalleryPage(
                    index = page.pageIndex.toInt(),
                    image = GalleryPageImage.Remote(
                        thumbnailUrl = NHentaiUrl.galleryPageThumbnail(
                            mediaId = mediaId,
                            pageNumber = page.pageIndex.toInt() + 1,
                            fileType = fileExtension
                        ),
                        originalUrl = NHentaiUrl.galleryPage(
                            mediaId = mediaId,
                            pageNumber = page.pageIndex.toInt() + 1,
                            fileType = fileExtension
                        ),
                    ),
                    resolution = Resolution(page.width.toInt(), page.height.toInt())
                )
            }
        }
    }
}
