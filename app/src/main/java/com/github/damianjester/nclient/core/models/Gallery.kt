package com.github.damianjester.nclient.core.models

import io.ktor.http.Url
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class GalleryId(val value: Long) {
    override fun toString(): String {
        return "#$value"
    }
}

@JvmInline
@Serializable
value class MediaId(val value: Long)

@Serializable
data class GalleryTitle(
    val pretty: String,
    val english: String?,
    val japanese: String?,
)

@Serializable
data class GalleryCover(
    val thumbnailUrl: Url,
    val originalUrl: Url,
)

@Serializable
data class Gallery(
    val id: GalleryId,
    val title: GalleryTitle,
    val cover: GalleryCover,
    val language: GalleryLanguage,
    val updated: LocalDateTime,
    val favoriteCount: Int,
)

data class GalleryDetails(
    val gallery: Gallery,
    val pages: List<GalleryPage>,
    val tags: GalleryTags,
    val related: List<RelatedGallery>,
)

data class GallerySummary(
    val id: GalleryId,
    val title: String,
    val language: GalleryLanguage,
    val images: GallerySummaryImages,
)

sealed interface GallerySummaryImages {
    data class Remote(val thumbnail: GalleryImage.Remote) : GallerySummaryImages

    data class Local(
        val cover: GalleryImage.Local,
        val thumbnail: GalleryImage.Local,
    ) : GallerySummaryImages
}

data class RelatedGallery(
    val id: GalleryId,
    val title: String,
    val language: GalleryLanguage,
    val image: GalleryImage.Remote,
)
