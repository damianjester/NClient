package com.github.damianjester.nclient.repo

import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.GalleryHistoryVisitEntity
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryVisit
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.db.orUpdate
import com.github.damianjester.nclient.mappers.toGalleryHistoryWithTagIds
import com.github.damianjester.nclient.mappers.toGalleryVisit
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

interface GalleryHistoryRepository {
    suspend fun getVisits(query: GalleryHistoryQuery): List<GalleryVisit>

    suspend fun upsert(id: GalleryId, instant: Instant)

    suspend fun deleteAll()
}

class SqlDelightGalleryHistoryRepository(
    private val database: Database,
    private val dispatchers: NClientDispatchers,
) : GalleryHistoryRepository {
    private val queries
        get() = database.galleryHistoryVisitEntityQueries

    override suspend fun getVisits(query: GalleryHistoryQuery): List<GalleryVisit> = withContext(dispatchers.IO) {
        val (limit, pageOffset, sort) = query

        val sortNum = when (sort.type) {
            GalleryHistoryQuery.SortType.ViewCount -> when (sort.order) {
                SortOrder.ASC -> 1
                SortOrder.DESC -> 2
            }
            GalleryHistoryQuery.SortType.LastVisit -> when (sort.order) {
                SortOrder.ASC -> 3
                SortOrder.DESC -> 4
            }
        }.toLong()

        queries.selectHistoryVisits(sort = sortNum, limit = limit.toLong(), offset = (limit * pageOffset).toLong())
            .executeAsList()
            .toGalleryHistoryWithTagIds()
            .map { it.toGalleryVisit() }
    }

    override suspend fun upsert(id: GalleryId, instant: Instant): Unit = withContext(dispatchers.IO) {
        database.transaction {
            val entity = GalleryHistoryVisitEntity(
                galleryId = id.value,
                lastVisitInstant = instant.epochSeconds,
                visitCount = 1
            )

            queries.insertVisit(entity)
                .orUpdate {
                    val visitCount = queries.selectVisitCount(id.value).executeAsOne()
                    queries.updateVisit(instant.epochSeconds, visitCount + 1, id.value)
                }
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatchers.IO) {
            queries.deleteAll()
        }
    }
}
