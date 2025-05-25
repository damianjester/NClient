package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.GalleryId
import com.github.damianjester.nclient.GalleryPage
import com.github.damianjester.nclient.GalleryPageImage
import com.github.damianjester.nclient.GalleryPageImageFileType
import com.github.damianjester.nclient.MediaId
import com.github.damianjester.nclient.Resolution
import com.github.damianjester.nclient.db.GalleryRepository
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

interface GalleryPagesFetcher {
    fun fetch(id: GalleryId): Flow<List<GalleryPage>>
}

class DefaultGalleryPagesFetcher(
    private val galleryRepository: GalleryRepository,
) : GalleryPagesFetcher {
    override fun fetch(id: GalleryId): Flow<List<GalleryPage>> {
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
