package com.github.damianjester.nclient.core

import io.ktor.http.Url
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.io.File
import kotlin.time.Duration

sealed interface Result<out T, out E> {
    data class Ok<T>(val value: T) : Result<T, Nothing>

    data class Err<E>(val cause: E) : Result<Nothing, E>
}

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
    interface KnownLanguage {
        val id: GalleryTagId
    }

    data object English : GalleryLanguage, KnownLanguage {
        override val id = GalleryTagId(12227L)
    }

    data object Chinese : GalleryLanguage, KnownLanguage {
        override val id = GalleryTagId(29963L)
    }

    data object Japanese : GalleryLanguage, KnownLanguage {
        override val id = GalleryTagId(6346L)
    }

    data object Unknown : GalleryLanguage

    companion object {
        fun fromLongTagId(ids: List<Long>): GalleryLanguage {
            return fromTagId(ids.map { GalleryTagId(it) })
        }

        fun fromTagId(ids: List<GalleryTagId>): GalleryLanguage {
            return when {
                ids.contains(Japanese.id) -> Japanese
                ids.contains(English.id) -> English
                ids.contains(Chinese.id) -> Chinese
                else -> Unknown
            }
        }
    }
}

sealed interface GalleryImage {
    data class Remote(val url: Url) : GalleryImage

    data class Local(val file: File) : GalleryImage
}

sealed interface GallerySearchItemImages {
    data class Remote(val thumbnail: GalleryImage.Remote) : GallerySearchItemImages

    data class Local(
        val cover: GalleryImage.Local,
        val thumbnail: GalleryImage.Local,
    ) : GallerySearchItemImages
}

data class GallerySearchItem(
    val id: GalleryId,
    val title: String,
    val language: GalleryLanguage,
    val images: GallerySearchItemImages,
)

data class RelatedGallery(
    val id: GalleryId,
    val title: String,
    val language: GalleryLanguage,
    val image: GalleryImage.Remote,
)

data class GalleryTitle(
    val pretty: String,
    val english: String?,
    val japanese: String?,
)

data class GalleryCover(
    val thumbnailUrl: Url,
    val originalUrl: Url,
)

data class Resolution(val width: Int, val height: Int)

data class GalleryPage(
    val index: Int,
    val image: GalleryPageImages,
    val resolution: Resolution,
)

sealed class GalleryImageFileType() {
    sealed interface WebpVariant {
        val hasWebpExtension: Boolean
    }

    data class GIF(override val hasWebpExtension: Boolean = false) : GalleryImageFileType(), WebpVariant

    data class PNG(override val hasWebpExtension: Boolean = false) : GalleryImageFileType(), WebpVariant

    data class JPG(override val hasWebpExtension: Boolean = false) : GalleryImageFileType(), WebpVariant

    data class WEBP(override val hasWebpExtension: Boolean = false) : GalleryImageFileType(), WebpVariant

    data class Unknown(val type: String) : GalleryImageFileType()

    companion object {
        fun fromType(type: String): GalleryImageFileType {
            return when (type.lowercase()) {
                "g" -> GIF()
                "p" -> PNG()
                "j" -> JPG()
                "w" -> WEBP()
                else -> Unknown(type)
            }
        }

        fun fromFileExtension(extension: String): GalleryImageFileType {
            return when (extension.lowercase()) {
                "gif" -> GIF()
                "png" -> PNG()
                "jpg" -> JPG()
                "webp" -> WEBP()
                "jpg.webp" -> JPG(hasWebpExtension = true)
                "gif.webp" -> GIF(hasWebpExtension = true)
                "png.webp" -> PNG(hasWebpExtension = true)
                "webp.webp" -> WEBP(hasWebpExtension = true)
                else -> Unknown(extension)
            }
        }
    }

    fun toFileExtension(): String {
        var extension = when (this) {
            is GIF -> "gif"
            is JPG -> "jpg"
            is PNG -> "png"
            is WEBP -> "webp"
            is Unknown -> type
        }

        // Append additional ".webp" extension
        // For example: https://t1.nhentai.net/galleries/[mediaId]/thumb.jpg.webp
        if (this is WebpVariant && hasWebpExtension) {
            extension += ".webp"
        }

        return extension
    }
}

sealed interface GalleryPageImages {
    val remoteThumbnail: GalleryImage.Remote
    val remoteOriginal: GalleryImage.Remote

    data class Remote(
        override val remoteThumbnail: GalleryImage.Remote,
        override val remoteOriginal: GalleryImage.Remote,
    ) : GalleryPageImages

    data class Local(
        override val remoteThumbnail: GalleryImage.Remote,
        override val remoteOriginal: GalleryImage.Remote,
        val localThumbnail: GalleryImage.Local,
        val localOriginal: GalleryImage.Local,
    ) : GalleryPageImages
}

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
    val count: Int,
)

data class Gallery(
    val id: GalleryId,
    val title: GalleryTitle,
    val cover: GalleryCover,
    val updated: LocalDateTime,
    val favoriteCount: Int,
)

data class GalleryDetails(
    val gallery: Gallery,
    val pages: List<GalleryPage>,
    val tags: GalleryTags,
    val related: List<RelatedGallery>,
)

data class GalleryTags(
    val all: List<GalleryTag>,
    val parody: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Parody) },
    val character: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Character) },
    val general: List<GalleryTag> = all.filter { (it.type == GalleryTagType.General) },
    val artist: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Artist) },
    val group: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Group) },
    val language: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Language) },
    val category: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Category) },
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
    val body: String,
)

data class CommentPoster(
    val id: UserId,
    val username: String,
    val avatar: Url?,
)
