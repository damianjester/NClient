package com.github.damianjester.nclient.repo

import androidx.paging.PagingSource
import app.cash.sqldelight.Query
import app.cash.sqldelight.paging3.QueryPagingSource
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.GalleryHistoryVisitEntity
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryVisit
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.db.orUpdate
import com.github.damianjester.nclient.mappers.mapRowToGalleryVisit
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

interface GalleryHistoryRepository {
    fun getVisits(query: GalleryHistoryQuery): PagingSource<Int, GalleryVisit>

    suspend fun upsert(id: GalleryId, instant: Instant)

    suspend fun deleteAll()
}

class SqlDelightGalleryHistoryRepository(
    private val database: Database,
    private val dispatchers: NClientDispatchers,
) : GalleryHistoryRepository {
    private val queries
        get() = database.galleryHistoryVisitEntityQueries

    override fun getVisits(query: GalleryHistoryQuery): PagingSource<Int, GalleryVisit> {
        val sort = query.sort

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

        val queryProvider: (limit: Long, offset: Long) -> Query<GalleryVisit> = { limit, offset ->
            queries.selectHistoryVisits(
                sort = sortNum,
                limit = limit,
                offset = offset,
                mapper = ::mapRowToGalleryVisit
            )
        }

        return QueryPagingSource(
            countQuery = queries.countHistoryVisits(),
            transacter = queries,
            context = dispatchers.IO,
            queryProvider = queryProvider
        )
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
