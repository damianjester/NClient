package com.github.damianjester.nclient.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.GalleryDetailsEntity
import com.github.damianjester.nclient.GalleryDetailsEntityQueries
import com.github.damianjester.nclient.GalleryEntity
import com.github.damianjester.nclient.GalleryEntityQueries
import com.github.damianjester.nclient.GalleryHasTag
import com.github.damianjester.nclient.GalleryHasTagQueries
import com.github.damianjester.nclient.GalleryPageEntity
import com.github.damianjester.nclient.GalleryPageEntityQueries
import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.TagEntityQueries
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryTagId
import com.github.damianjester.nclient.core.MediaId
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface GalleryRepository {
    fun selectAll(): Flow<List<GalleryEntityWithTagIds>>

    fun selectGalleryDetails(id: GalleryId): Flow<CompleteGallery>

    fun selectPagesForGallery(id: GalleryId): Flow<List<GalleryPageEntity>>

    suspend fun countPagesForGallery(id: GalleryId): Int

    suspend fun selectMediaIdForGallery(id: GalleryId): MediaId

    suspend fun insertAll(
        galleries: List<GalleryEntity>,
        galleryHasTag: Map<GalleryEntity, List<GalleryTagId>>,
    )

    suspend fun insertDetails(
        details: GalleryDetailsEntity,
        pages: List<GalleryPageEntity>,
        tags: List<TagEntity>,
    )
}

data class GalleryEntityWithTagIds(
    val id: Long,
    val title: String,
    val mediaId: Long,
    val coverThumbnailUrl: String,
    val tagIds: List<Long>,
)

data class CompleteGallery(
    val id: Long,
    val mediaId: Long,
    val numFavorites: Long,
    val prettyTitle: String,
    val englishTitle: String?,
    val japaneseTitle: String?,
    val uploadDate: Long,
)

class SqlDelightGalleryRepository(
    private val database: Database,
    private val dispatchers: NClientDispatchers,
) : GalleryRepository {
    private val galleryQueries: GalleryEntityQueries
        get() = database.galleryEntityQueries
    private val galleryDetailsEntityQueries: GalleryDetailsEntityQueries
        get() = database.galleryDetailsEntityQueries
    private val galleryPageQueries: GalleryPageEntityQueries
        get() = database.galleryPageEntityQueries
    private val tagEntityQueries: TagEntityQueries
        get() = database.tagEntityQueries
    private val tagAssociationQueries: GalleryHasTagQueries
        get() = database.galleryHasTagQueries

    override fun selectAll(): Flow<List<GalleryEntityWithTagIds>> {
        return galleryQueries.selectAllTest().asFlow()
            .mapToList(dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.galleryId }
                    .map { (id, rows) ->
                        val first = rows.first()
                        val tagIds = rows.map { it.tagId }
                        GalleryEntityWithTagIds(
                            id,
                            first.title,
                            first.mediaId,
                            first.coverThumbnailUrl,
                            tagIds
                        )
                    }
            }
    }

    override fun selectGalleryDetails(id: GalleryId): Flow<CompleteGallery> {
        return galleryDetailsEntityQueries.selectDetails(id.value) {
            mediaId,
            numFavorites,
            prettyTitle,
            englishTitle,
            japaneseTitle,
            uploadDate
            ->
            CompleteGallery(
                id = id.value,
                mediaId = mediaId,
                numFavorites = numFavorites,
                prettyTitle = prettyTitle,
                englishTitle = englishTitle,
                japaneseTitle = japaneseTitle,
                uploadDate = uploadDate
            )
        }
            .asFlow()
            .mapToOneNotNull(dispatchers.IO)
    }

    override fun selectPagesForGallery(id: GalleryId): Flow<List<GalleryPageEntity>> {
        return galleryPageQueries.selectPagesForGallery(id.value).asFlow()
            .mapToList(dispatchers.IO)
    }

    override suspend fun selectMediaIdForGallery(id: GalleryId): MediaId {
        return MediaId(database.galleryEntityQueries.selectMediaId(id.value).executeAsOne())
    }

    override suspend fun countPagesForGallery(id: GalleryId): Int = withContext(dispatchers.IO) {
        database.galleryPageEntityQueries.countPagesForGallery(id.value)
            .executeAsOne()
            .toInt()
    }

    override suspend fun insertAll(
        galleries: List<GalleryEntity>,
        galleryHasTag: Map<GalleryEntity, List<GalleryTagId>>,
    ) = withContext(dispatchers.IO) {
        database.transaction {
            galleries.forEach { galleryQueries.insertGallery(it) }
            galleryHasTag
                .map { (gal, v) -> v.map { tagId -> GalleryHasTag(gal.id, tagId.value) } }
                .flatten()
                .forEach { tagAssociationQueries.insertAssociation(it) }
        }
    }

    override suspend fun insertDetails(
        details: GalleryDetailsEntity,
        pages: List<GalleryPageEntity>,
        tags: List<TagEntity>,
    ) = withContext(dispatchers.IO) {
        // TODO: Make tag associations if needed

        database.transaction {
            galleryDetailsEntityQueries.insertDetails(details)
            pages.forEach { galleryPageQueries.insertPage(it) }
            tags.forEach { tagEntityQueries.insertTag(it) }
        }
    }
}
