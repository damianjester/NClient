package com.github.damianjester.nclient.core.models

import io.ktor.http.Url
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class GalleryTagId(val value: Long)

data class GalleryTag(
    val id: GalleryTagId,
    val type: GalleryTagType,
    val name: String,
    val url: Url,
    val count: Int,
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
