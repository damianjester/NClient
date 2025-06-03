package com.github.damianjester.nclient.db.mappers

import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.core.models.GalleryTag
import com.github.damianjester.nclient.core.models.GalleryTagId
import com.github.damianjester.nclient.core.models.GalleryTagType
import com.github.damianjester.nclient.core.models.GalleryTags
import com.github.damianjester.nclient.net.models.GalleryDetailsResponse
import io.ktor.http.Url

fun GalleryDetailsResponse.Success.toGalleryDetailsTags() =
    gallery.tags
        .map { t ->
            TagEntity(
                id = t.id.value,
                type = t.type,
                name = t.name,
                count = t.count.toLong(),
                urlPath = t.url
            )
        }

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
