package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.GalleryRepository
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val LANGUAGE_JAPANESE = 6346L
const val LANGUAGE_ENGLISH = 12227L
const val LANGUAGE_CHINESE = 29963L

interface GallerySearchObserver {
    fun galleries(): Flow<List<GallerySearchItem>>
}

class DefaultGallerySearchObserver(
    private val galleryRepository: GalleryRepository,
) : GallerySearchObserver {
    override fun galleries(): Flow<List<GallerySearchItem>> {
        return galleryRepository.selectAll()
            .map { galleries ->
                galleries.map { gal ->
                    GallerySearchItem(
                        id = GalleryId(gal.id),
                        title = gal.title,
                        language = determineGalleryLanguage(gal.tagIds),
                        image = GalleryGridItemImage.Remote(
                            thumbnailUrl = Url(gal.coverThumbnailUrl),
                            coverUrl = Url("https://t1.nhentai.net/galleries/${gal.mediaId}/cover.webp") // TODO: Extract to function
                        ),
                    )
                }
            }
    }

    private fun determineGalleryLanguage(tagIds: List<Long>): GalleryLanguage {
        return when {
            tagIds.contains(LANGUAGE_JAPANESE) -> GalleryLanguage.Japanese
            tagIds.contains(LANGUAGE_ENGLISH) -> GalleryLanguage.English
            tagIds.contains(LANGUAGE_CHINESE) -> GalleryLanguage.Chinese
            else -> GalleryLanguage.Unknown
        }
    }
}
