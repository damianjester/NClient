package com.github.damianjester.nclient.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.GalleryDetailsEntity
import com.github.damianjester.nclient.GalleryHasRelated
import com.github.damianjester.nclient.GalleryHasRelatedQueries
import com.github.damianjester.nclient.GalleryHasTag
import com.github.damianjester.nclient.GalleryPageEntity
import com.github.damianjester.nclient.GalleryPageEntityQueries
import com.github.damianjester.nclient.GalleryQueries
import com.github.damianjester.nclient.GalleryQueryEntity
import com.github.damianjester.nclient.GalleryQueryQueries
import com.github.damianjester.nclient.GallerySummaryEntity
import com.github.damianjester.nclient.QueryHasGallery
import com.github.damianjester.nclient.SelectSummaryWithDetails
import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.TagEntityQueries
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryTagId
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface GalleryRepository {
    fun selectSummariesForQuery(query: GalleryQueryEntity): Flow<List<GalleryWithTagIds>>

    fun selectGalleryDetails(id: GalleryId): Flow<SelectSummaryWithDetails>

    fun selectPagesForGallery(id: GalleryId): Flow<List<GalleryPageWithMediaId>>

    suspend fun countPagesForGallery(id: GalleryId): Int

    fun selectRelatedGalleries(id: GalleryId): Flow<List<GalleryWithTagIds>>

    suspend fun insertGallerySummaries(
        query: GalleryQueryEntity,
        galleries: List<GallerySummaryEntity>,
        galleryHasTag: Map<GallerySummaryEntity, List<GalleryTagId>>,
    )

    suspend fun insertGalleryWithDetails(
        summary: GallerySummaryEntity,
        details: GalleryDetailsEntity,
        pages: List<GalleryPageEntity>,
        tags: List<TagEntity>,
        related: List<Pair<GallerySummaryEntity, List<GalleryTagId>>>,
    )
}

data class GalleryWithTagIds(
    val id: Long,
    val title: String,
    val mediaId: Long,
    val coverThumbnailFileExtension: String?,
    val tagIds: List<Long>,
)

data class GalleryPageWithMediaId(
    val galleryId: Long,
    val pageIndex: Long,
    val fileExtension: String,
    val width: Long,
    val height: Long,
    val mediaId: Long,
)

class SqlDelightGalleryRepository(
    private val database: Database,
    private val dispatchers: NClientDispatchers,
    private val logger: Logger,
) : GalleryRepository {
    private val galleryQueryQueries: GalleryQueryQueries
        get() = database.galleryQueryQueries
    private val galleryQueries: GalleryQueries
        get() = database.galleryQueries
    private val galleryPageQueries: GalleryPageEntityQueries
        get() = database.galleryPageEntityQueries
    private val tagQueries: TagEntityQueries
        get() = database.tagEntityQueries
    private val galleryHasRelatedQueries: GalleryHasRelatedQueries
        get() = database.galleryHasRelatedQueries

    override fun selectSummariesForQuery(query: GalleryQueryEntity): Flow<List<GalleryWithTagIds>> {
        return galleryQueries.selectSummariesFoQuery(query.id).asFlow()
            .mapToList(dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.galleryId }
                    .map { (id, rows) ->
                        val first = rows.first()
                        val tagIds = rows.map { it.tagId }
                        GalleryWithTagIds(
                            id,
                            first.prettyTitle,
                            first.mediaId,
                            first.coverThumbnailFileExtension,
                            tagIds
                        )
                    }
            }
    }

    override fun selectGalleryDetails(id: GalleryId): Flow<SelectSummaryWithDetails> {
        return galleryQueries.selectSummaryWithDetails(id.value)
            .asFlow()
            .mapToOneNotNull(dispatchers.IO)
    }

    override fun selectPagesForGallery(id: GalleryId): Flow<List<GalleryPageWithMediaId>> {
        return galleryPageQueries.selectPagesForGallery(id.value) {
            galleryId: Long,
            pageIndex: Long,
            fileExtension: String,
            width: Long,
            height: Long,
            mediaId: Long
            ->

            GalleryPageWithMediaId(
                galleryId = galleryId,
                pageIndex = pageIndex,
                fileExtension = fileExtension,
                width = width,
                height = height,
                mediaId = mediaId
            )
        }
            .asFlow()
            .mapToList(dispatchers.IO)
    }

    override fun selectRelatedGalleries(id: GalleryId): Flow<List<GalleryWithTagIds>> {
        return galleryHasRelatedQueries.selectRelatedForGallery(id.value)
            .asFlow()
            .mapToList(dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.id }
                    .map { (id, rows) ->
                        val first = rows.first()
                        val tagIds = rows.map { it.tagId }
                        GalleryWithTagIds(
                            id,
                            first.prettyTitle,
                            first.mediaId,
                            first.coverThumbnailFileExtension,
                            tagIds
                        )
                    }
            }
    }

    override suspend fun countPagesForGallery(id: GalleryId): Int = withContext(dispatchers.IO) {
        database.galleryPageEntityQueries.countPagesForGallery(id.value)
            .executeAsOne()
            .toInt()
    }

    override suspend fun insertGallerySummaries(
        query: GalleryQueryEntity,
        galleries: List<GallerySummaryEntity>,
        galleryHasTag: Map<GallerySummaryEntity, List<GalleryTagId>>,
    ) = withContext(dispatchers.IO) {

        if (galleries.isEmpty()) {
            return@withContext
        }

        logger.i(
            LogTags.gallery,
            "Inserting ${galleries.size} gallery entities and " +
                "${galleryHasTag.values.size} has-tag associations for query $query."
        )
        database.transaction {
            galleryQueryQueries.insertQuery(query)
            galleries.forEachIndexed { i, gal ->
                galleryQueries.insertSummary(gal)
                galleryQueryQueries.insertHasGallery(QueryHasGallery(query.id, gal.id, i.toLong()))
            }
            galleryHasTag
                .map { (gal, v) -> v.map { tagId -> GalleryHasTag(gal.id, tagId.value) } }
                .flatten()
                .forEach { galleryQueries.insertHasTag(it) }
        }
    }

    override suspend fun insertGalleryWithDetails(
        summary: GallerySummaryEntity,
        details: GalleryDetailsEntity,
        pages: List<GalleryPageEntity>,
        tags: List<TagEntity>,
        related: List<Pair<GallerySummaryEntity, List<GalleryTagId>>>,
    ) = withContext(dispatchers.IO) {
        logger.i(
            LogTags.gallery,
            "Inserting details for gallery #${summary.id} with " +
                "${pages.size} pages, ${tags.size} tags and ${related.size} related galleries."
        )

        database.transaction {
            galleryQueries.insertSummary(summary)
            galleryQueries.insertDetails(details)
            pages.forEach { galleryPageQueries.insertPage(it) }
            tags.forEach { tag ->
                tagQueries.insertTag(tag)
                galleryQueries.insertHasTag(GalleryHasTag(summary.id, tag.id))
            }
            related.forEachIndexed { i, (related, tagIds) ->
                galleryQueries.insertSummary(related)
                galleryQueries.insertHasRelated(
                    GalleryHasRelated(
                        galleryId = summary.id,
                        relatedId = related.id,
                        orderIndex = i.toLong()
                    )
                )
                tagIds.forEach { tagId ->
                    galleryQueries.insertHasTag(GalleryHasTag(related.id, tagId.value))
                }
            }
        }
    }
}
