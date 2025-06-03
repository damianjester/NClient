package com.github.damianjester.nclient.core.models

import io.ktor.http.Url
import java.io.File

sealed interface GalleryImage {
    data class Remote(val url: Url) : GalleryImage

    data class Local(val file: File) : GalleryImage
}

data class Resolution(val width: Int, val height: Int)

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
