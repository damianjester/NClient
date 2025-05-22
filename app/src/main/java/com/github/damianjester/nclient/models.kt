package com.github.damianjester.nclient

import io.ktor.http.Url
import kotlinx.serialization.Serializable
import java.io.File

@JvmInline
@Serializable
value class GalleryId(val value: Long)

@JvmInline
@Serializable
value class GalleryTagId(val id: Long)

sealed interface GalleryLanguage {
    data object English : GalleryLanguage
    data object Chinese : GalleryLanguage
    data object Japanese : GalleryLanguage
    data class Unknown(val locale: String) : GalleryLanguage
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

data class GalleryGridItem(
    val id: GalleryId,
    val title: String,
    val language: GalleryLanguage,
    val image: GalleryGridItemImage,
)
