package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GalleryPagesObserver {
    fun pages(id: GalleryId): Flow<List<GalleryPage>>
}

class DefaultGalleryPagesObserver(
    private val galleryRepository: GalleryRepository,
) : GalleryPagesObserver {
    override fun pages(id: GalleryId): Flow<List<GalleryPage>> {
        return galleryRepository.selectPagesForGallery(id)
            .map { pages ->
                pages.map { page ->

                    val fileType = GalleryImageFileType.fromFileExtension(page.fileExtension)

                    GalleryPage(
                        index = page.pageIndex.toInt(),
                        image = GalleryPageImages.Remote(
                            remoteThumbnail = GalleryImage.Remote(
                                NHentaiUrl.galleryPageThumbnail(
                                    mediaId = MediaId(page.mediaId),
                                    pageNumber = page.pageIndex.toInt() + 1,
                                    fileType = fileType
                                )
                            ),
                            remoteOriginal = GalleryImage.Remote(
                                NHentaiUrl.galleryPage(
                                    mediaId = MediaId(page.mediaId),
                                    pageNumber = page.pageIndex.toInt() + 1,
                                    fileType = fileType
                                )
                            ),
                        ),
                        resolution = Resolution(page.width.toInt(), page.height.toInt())
                    )
                }
            }
    }
}
