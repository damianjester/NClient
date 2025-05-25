package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.GalleryRepository
import io.ktor.http.Url
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
                        thumbnailUrl = Url("https://t1.nhentai.net/galleries/${mediaId.value}/${page.pageIndex + 1}t.${fileExtension.toThumbnailFileExtension()}"),
                        originalUrl = Url("https://i1.nhentai.net/galleries/${mediaId.value}/${page.pageIndex + 1}.${fileExtension.toOriginalFileExtension()}"),
                    ),
                    resolution = Resolution(page.width.toInt(), page.height.toInt())
                )
            }
        }
    }
}
