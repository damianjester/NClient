package com.github.damianjester.nclient.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.TagEntityQueries
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getTagsForGallery(id: GalleryId): Flow<List<TagEntity>>
}

class SqlDelightTagRepository(
    private val database: Database,
    private val dispatchers: NClientDispatchers,
) : TagRepository {
    private val queries: TagEntityQueries
        get() = database.tagEntityQueries

    override fun getTagsForGallery(id: GalleryId): Flow<List<TagEntity>> {
        return queries.selectTagsForGallery(id.value).asFlow()
            .mapToList(dispatchers.IO)
    }
}
