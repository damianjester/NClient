package com.github.damianjester.nclient.core

import io.ktor.http.Url
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.io.File
import kotlin.time.Duration

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

@JvmInline
@Serializable
value class GalleryTagId(val value: Long)

sealed interface GalleryLanguage {
    data object English : GalleryLanguage
    data object Chinese : GalleryLanguage
    data object Japanese : GalleryLanguage
    data object Unknown : GalleryLanguage
}

sealed interface GalleryGridItemImage {

    data class Remote(
        val thumbnailUrl: Url,
        val coverUrl: Url
    ) : GalleryGridItemImage

    data class Local(
        val thumbnailUrl: Url,
        val thumbnailFile: File,
        val coverUrl: Url,
        val coverFile: File,
    ) : GalleryGridItemImage
}

data class GallerySearchItem(
    val id: GalleryId,
    val title: String,
    val language: GalleryLanguage,
    val image: GalleryGridItemImage,
)

data class RelatedGallery(
    val id: GalleryId,
    val title: String,
    val language: GalleryLanguage,
    val image: GalleryGridItemImage.Remote,
)

data class GalleryTitle(
    val english: String?,
    val japanese: String?,
    val pretty: String,
)

data class GalleryCover(
    val thumbnailUrl: Url,
    val originalUrl: Url
)

data class Resolution(val width: Int, val height: Int)

data class GalleryPage(
    val index: Int,
    val image: GalleryPageImage,
    val resolution: Resolution
)

sealed class GalleryPageImageFileType() {

    sealed interface WebpVariant {
        val webp: Boolean
    }

    data class GIF(override val webp: Boolean = false) : GalleryPageImageFileType(), WebpVariant
    data class PNG(override val webp: Boolean = false) : GalleryPageImageFileType(), WebpVariant
    data class JPG(override val webp: Boolean = false) : GalleryPageImageFileType(), WebpVariant
    data class WEBP(override val webp: Boolean = false) : GalleryPageImageFileType(), WebpVariant
    data class Unknown(val type: String) : GalleryPageImageFileType()

    companion object {
        fun fromType(type: String): GalleryPageImageFileType {
            return when (type.lowercase()) {
                "gif", "g" -> GIF()
                "png", "p" -> PNG()
                "jpg", "j" -> JPG()
                "webp", "w" -> WEBP()
                "jpg.webp" -> JPG(webp = true)
                "gif.webp" -> GIF(webp = true)
                "png.webp" -> PNG(webp = true)
                "webp.webp" -> WEBP(webp = true)
                else -> Unknown(type)
            }
        }
    }

    fun toOriginalFileExtension(): String {

        var extension = when (this) {
            is GIF -> "gif"
            is JPG -> "jpg"
            is PNG -> "png"
            is WEBP -> "webp"
            is Unknown -> type
        }

        return extension
    }

    fun toThumbnailFileExtension(): String {
        var extension = when (this) {
            is GIF -> "gif"
            is JPG -> "jpg"
            is PNG -> "png"
            is WEBP -> "webp"
            is Unknown -> type
        }

        if (this is WebpVariant) {
            extension += ".webp"
        }

        return extension
    }

}

sealed interface GalleryPageImage {
    val thumbnailUrl: Url
    val originalUrl: Url

    data class Remote(
        override val thumbnailUrl: Url,
        override val originalUrl: Url
    ) : GalleryPageImage

    data class Local(
        override val thumbnailUrl: Url,
        override val originalUrl: Url,
        val thumbnailFile: File,
        val originalFile: File,
    ) : GalleryPageImage
}

fun GalleryPage.webpageUrl(id: GalleryId): Url =
    Url("https://nhentai.net/g/${id.value}/${index + 1}/")

sealed interface GalleryTagType {
    data object General : GalleryTagType
    data object Language : GalleryTagType
    data object Category : GalleryTagType
    data object Parody : GalleryTagType
    data object Character : GalleryTagType
    data object Artist : GalleryTagType
    data object Group : GalleryTagType
    data class Unknown(val value: String) : GalleryTagType
}

data class GalleryTag(
    val id: GalleryTagId,
    val type: GalleryTagType,
    val name: String,
    val url: Url,
    val count: Int
)

data class Gallery(
    val id: GalleryId,
    val title: GalleryTitle,
    val cover: GalleryCover,
    val updated: LocalDateTime,
    val favoriteCount: Int,
)

@JvmInline
value class CommentId(val value: Long)

@JvmInline
value class UserId(val value: Long)

data class Comment(
    val id: CommentId,
    val poster: CommentPoster,
    val date: LocalDateTime,
    val elapsedTime: Duration,
    val body: String
)

data class CommentPoster(
    val id: UserId,
    val username: String,
    val avatar: Url?
)
