package com.github.damianjester.nclient.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneNotNull
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.GalleryHasRelated
import com.github.damianjester.nclient.GalleryHasRelatedQueries
import com.github.damianjester.nclient.GalleryHasTag
import com.github.damianjester.nclient.GalleryPageEntityQueries
import com.github.damianjester.nclient.GalleryQueries
import com.github.damianjester.nclient.GalleryQueryEntity
import com.github.damianjester.nclient.GalleryQueryQueries
import com.github.damianjester.nclient.QueryHasGallery
import com.github.damianjester.nclient.TagEntityQueries
import com.github.damianjester.nclient.core.models.GalleryDetails
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryTitle
import com.github.damianjester.nclient.db.orUpdate
import com.github.damianjester.nclient.db.selectPagesWithMediaIdForGallery
import com.github.damianjester.nclient.db.updateDetails
import com.github.damianjester.nclient.db.updateSummary
import com.github.damianjester.nclient.db.updateTag
import com.github.damianjester.nclient.mappers.mapRowToGallerySummary
import com.github.damianjester.nclient.mappers.mapRowToRelatedGallery
import com.github.damianjester.nclient.mappers.toGallery
import com.github.damianjester.nclient.mappers.toGalleryDetailsEntity
import com.github.damianjester.nclient.mappers.toGalleryDetailsPages
import com.github.damianjester.nclient.mappers.toGalleryDetailsTags
import com.github.damianjester.nclient.mappers.toGalleryPage
import com.github.damianjester.nclient.mappers.toGallerySummary
import com.github.damianjester.nclient.mappers.toGallerySummaryEntityWithHasTags
import com.github.damianjester.nclient.mappers.toRelatedGalleries
import com.github.damianjester.nclient.mappers.toTag
import com.github.damianjester.nclient.mappers.toTags
import com.github.damianjester.nclient.net.models.GalleryDetailsResponse
import com.github.damianjester.nclient.net.models.GallerySummariesResponse
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

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

    override suspend fun selectSummariesForQuery(query: GalleryQueryEntity) = withContext(dispatchers.IO) {
        galleryQueries.selectSummariesFoQuery(query.id, ::mapRowToGallerySummary)
            .executeAsList()
    }

    override suspend fun selectGalleryDetails(id: GalleryId) = withContext(dispatchers.IO) {
        val tags = tagQueries.selectTagsForGallery(id.value).executeAsList()
            .map { it.toTag() }
            .toTags()

        val gallery = galleryQueries.selectSummaryWithDetails(id.value)
            .executeAsOne()
            .toGallery(tags.all.map { it.id })

        val pages = galleryPageQueries.selectPagesWithMediaIdForGallery(id)
            .executeAsList()
            .map { it.toGalleryPage() }

        val related = galleryHasRelatedQueries.selectRelatedForGallery(id.value, ::mapRowToRelatedGallery)
            .executeAsList()

        GalleryDetails(gallery, pages, tags, related)
    }

    override suspend fun selectGalleryPages(id: GalleryId) = withContext(dispatchers.IO) {
        galleryPageQueries.selectPagesWithMediaIdForGallery(id)
            .executeAsList()
            .map { it.toGalleryPage() }
    }

    override fun selectGalleryTitle(id: GalleryId) =
        galleryQueries.selectGalleryTitles(id.value) { pretty, english, japanese ->
            GalleryTitle(
                pretty = pretty,
                english = english,
                japanese = japanese
            )
        }.asFlow().mapToOneNotNull(dispatchers.IO)

    override suspend fun selectGalleryUpdatedAt(id: GalleryId) = withContext(dispatchers.IO) {
        galleryQueries.selectGalleryUpdatedAt(id.value).executeAsOneOrNull()
            ?.let { Instant.Companion.fromEpochSeconds(it) }
    }

    override suspend fun countPagesForGallery(id: GalleryId) = withContext(dispatchers.IO) {
        database.galleryPageEntityQueries.countPagesForGallery(id.value)
            .executeAsOne()
            .toInt()
    }

    override suspend fun replaceAllGallerySummaries(
        query: GalleryQueryEntity,
        response: GallerySummariesResponse,
    ) = withContext(dispatchers.IO) {
        val galleriesWithTags = response.toGallerySummaryEntityWithHasTags()

        if (galleriesWithTags.isEmpty()) {
            return@withContext
        }

        logger.i(
            LogTags.gallery,
            "Inserting ${galleriesWithTags.size} gallery entities and " +
                "${galleriesWithTags.map { it.second }.flatten().size} has-tag associations for query $query."
        )

        database.transaction {
            galleryQueryQueries.insertQuery(query)
            galleryQueryQueries.deleteForQuery(query.id)
            galleriesWithTags.forEachIndexed { index, (gallery, hasTags) ->
                galleryQueries.insertSummary(gallery)
                    .orUpdate { galleryQueries.updateSummary(gallery) }
                galleryQueryQueries.insertHasGallery(QueryHasGallery(query.id, gallery.id, index.toLong()))
                hasTags.forEach { galleryQueries.insertHasTag(it) }
            }
        }
    }

    override suspend fun upsertGalleryDetails(
        response: GalleryDetailsResponse,
    ) = withContext(dispatchers.IO) {
        val summary = response.toGallerySummary()
        val details = response.toGalleryDetailsEntity()
        val pages = response.toGalleryDetailsPages()
        val tags = response.toGalleryDetailsTags()
        val related = response.toRelatedGalleries()

        logger.i(
            LogTags.gallery,
            "Inserting details for gallery #${summary.id} with " +
                "${pages.size} pages, ${tags.size} tags and ${related.size} related galleries."
        )

        database.transaction {
            galleryQueries.insertSummary(summary)
                .orUpdate { galleryQueries.updateSummary(summary) }

            galleryQueries.insertDetails(details)
                .orUpdate { galleryQueries.updateDetails(details) }

            // TODO: DELETE+INSERT will remove users page bookmarks (future feature)
            galleryPageQueries.deletePages(summary.id)
            pages.forEach { galleryPageQueries.insertPage(it) }

            galleryQueries.deleteHasTags(summary.id)
            tags.forEach { tag ->
                tagQueries.insertTag(tag).orUpdate { tagQueries.updateTag(tag) }
                galleryQueries.insertHasTag(GalleryHasTag(summary.id, tag.id))
            }

            galleryQueries.deleteHasRelated(summary.id)
            related.forEachIndexed { i, (related, tagIds) ->
                galleryQueries.insertSummary(related)
                    .orUpdate { galleryQueries.updateSummary(related) }

                galleryQueries.insertHasRelated(
                    GalleryHasRelated(
                        galleryId = summary.id,
                        relatedId = related.id,
                        orderIndex = i.toLong()
                    )
                )

                galleryQueries.deleteHasTags(related.id)
                tagIds.forEach { tagId ->
                    galleryQueries.insertHasTag(GalleryHasTag(related.id, tagId.value))
                }
            }
        }
    }
}
