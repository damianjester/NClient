package com.github.damianjester.nclient.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.GalleryEntity
import com.github.damianjester.nclient.GalleryHasTag
import com.github.damianjester.nclient.GalleryTagId
import com.github.damianjester.nclient.NClientDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GalleryEntityRepository {
    fun selectAll(): Flow<List<GalleryEntityWithTagIds>>
    suspend fun insertAll(
        galleries: List<GalleryEntity>,
        galleryHasTag: Map<GalleryEntity, List<GalleryTagId>>
    )
}

data class GalleryEntityWithTagIds(
    val id: Long,
    val title: String,
    val mediaId: Long,
    val coverThumbnailUrl: String,
    val tagIds: List<Long>
)

class SqlDelightGalleryEntityRepository(
    private val database: Database,
    private val dispatchers: NClientDispatchers
) : GalleryEntityRepository {

    private val galleryQueries = database.galleryEntityQueries
    private val tagAssociationQueries = database.galleryHasTagQueries

    override fun selectAll(): Flow<List<GalleryEntityWithTagIds>> {
        return galleryQueries.selectAllTest().asFlow()
            .mapToList(dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.galleryId }
                    .map { (id, rows) ->
                        val first = rows.first()
                        val tagIds = rows.map { it.tagId }
                        GalleryEntityWithTagIds(id, first.title, first.mediaId, first.coverThumbnailUrl, tagIds)
                    }
            }
    }

    override suspend fun insertAll(
        galleries: List<GalleryEntity>,
        galleryHasTag: Map<GalleryEntity, List<GalleryTagId>>
    ) {
        database.transaction {
            galleries.forEach { galleryQueries.insertGallery(it) }
            galleryHasTag
                .map { (gal, v) -> v.map { tagId -> GalleryHasTag(gal.id, tagId.id) } }
                .flatten()
                .forEach { tagAssociationQueries.insertAssociation(it) }
        }
    }


}
