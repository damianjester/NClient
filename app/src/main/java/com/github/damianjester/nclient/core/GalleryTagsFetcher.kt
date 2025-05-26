package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.TagRepository
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GalleryTagsFetcher {
    fun fetch(id: GalleryId): Flow<List<GalleryTag>>
}

class DefaultGalleryTagsFetcher(
    private val repository: TagRepository,
) : GalleryTagsFetcher {
    override fun fetch(id: GalleryId): Flow<List<GalleryTag>> {
        return repository.getTagsForGallery(id)
            .map { tags ->
                tags.map { t ->
                    GalleryTag(
                        id = GalleryTagId(t.id),
                        type = when (t.type) {
                            // TODO: Double check these mappings
                            "tag" -> GalleryTagType.General
                            "language" -> GalleryTagType.Language
                            "category" -> GalleryTagType.Category
                            "parody" -> GalleryTagType.Parody
                            "character" -> GalleryTagType.Character
                            "artist" -> GalleryTagType.Artist
                            "group" -> GalleryTagType.Group
                            else -> GalleryTagType.Unknown(t.type)
                        },
                        name = t.name,
                        url = Url("https://nhentai.net/${t.urlPath}"),
                        count = t.count.toInt()
                    )
                }
            }
    }
}
