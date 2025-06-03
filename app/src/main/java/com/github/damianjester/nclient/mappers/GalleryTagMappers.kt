package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.core.models.GalleryTag
import com.github.damianjester.nclient.core.models.GalleryTagId
import com.github.damianjester.nclient.core.models.GalleryTagType
import com.github.damianjester.nclient.core.models.GalleryTags
import io.ktor.http.Url

fun TagEntity.toTag() =
    GalleryTag(
        id = GalleryTagId(id),
        type = when (type) {
            // TODO: Double check these mappings
            "tag" -> GalleryTagType.General
            "language" -> GalleryTagType.Language
            "category" -> GalleryTagType.Category
            "parody" -> GalleryTagType.Parody
            "character" -> GalleryTagType.Character
            "artist" -> GalleryTagType.Artist
            "group" -> GalleryTagType.Group
            else -> GalleryTagType.Unknown(type)
        },
        name = name,
        // TODO: Use NHentaiUrl
        url = Url("https://nhentai.net/$urlPath"),
        count = count.toInt()
    )

fun List<GalleryTag>.toTags() = GalleryTags(this)
