package com.github.damianjester.nclient.repo

import androidx.paging.PagingSource
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import app.cash.sqldelight.coroutines.mapToOneOrDefault
import app.cash.sqldelight.paging3.QueryPagingSource
import com.github.damianjester.nclient.CollectionHasGallery
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.core.models.CollectionDetailsQuery
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryCollectionQuery
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.core.models.GalleryImageFileType
import com.github.damianjester.nclient.core.models.GalleryLanguage
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.GallerySummaryImages
import com.github.damianjester.nclient.core.models.MediaId
import com.github.damianjester.nclient.mappers.toGalleryCollections
import com.github.damianjester.nclient.net.NHentaiUrl
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

interface GalleryCollectionRepository {
    fun getCollections(query: GalleryCollectionQuery): Flow<List<GalleryCollection>>

    fun getGalleries(query: CollectionDetailsQuery): PagingSource<Int, GallerySummary>

    fun getCollectionIds(id: GalleryId): Flow<List<GalleryCollectionId>>

    fun collectionName(id: GalleryCollectionId): Flow<String>

    fun isFavorite(id: GalleryId): Flow<Boolean>

    suspend fun createCollection(name: String, createdAt: Instant): GalleryCollectionId

    suspend fun rename(id: GalleryCollectionId, name: String)

    suspend fun addGallery(galleryId: GalleryId, collections: List<GalleryCollectionId>, instant: Instant)

    suspend fun removeGallery(galleryId: GalleryId, collections: List<GalleryCollectionId>, instant: Instant)

    suspend fun addAndRemoveGallery(
        galleryId: GalleryId,
        added: List<GalleryCollectionId>,
        removed: List<GalleryCollectionId>,
        timestamp: Instant
    )

    suspend fun deleteCollection(id: GalleryCollectionId)
}

class SqlDelightGalleryCollectionRepository(
    private val dispatchers: NClientDispatchers,
    private val database: Database,
) : GalleryCollectionRepository {
    private val queries
        get() = database.galleryCollectionQueries

    override fun getCollections(query: GalleryCollectionQuery): Flow<List<GalleryCollection>> {
        val sortOrderNum = query.sort.order.ordinal.toLong()

        val result = queries.run {
            when (query.sort.type) {
                GalleryCollectionQuery.SortType.ID -> selectCollectionsById(order = sortOrderNum)
                GalleryCollectionQuery.SortType.UpdatedAt -> selectCollectionsByUpdatedAt(order = sortOrderNum)
                GalleryCollectionQuery.SortType.Name -> selectCollectionsByName(order = sortOrderNum)
                GalleryCollectionQuery.SortType.Size -> selectCollectionsBySize(order = sortOrderNum)
            }
        }

        return result
            .asFlow()
            .mapToList(dispatchers.IO)
            .map { it.toGalleryCollections() }
    }

    override fun getGalleries(query: CollectionDetailsQuery): PagingSource<Int, GallerySummary> {
        val (id, sort) = query
        val (type, order) = sort

        val queryProvider: (limit: Long, offset: Long) -> Query<GallerySummary> = { limit, offset ->
            queries.run {
                when (type) {
                    CollectionDetailsQuery.SortType.Added ->
                        selectGalleriesForCollectionByCreatedAt(id.value, order.ordinal.toLong(), limit, offset, ::summary)
                    CollectionDetailsQuery.SortType.Title ->
                        selectGalleriesForCollectionByTitle(id.value, order.ordinal.toLong(), limit, offset, ::summary)
                    CollectionDetailsQuery.SortType.ID ->
                        selectGalleriesForCollectionById(id.value, order.ordinal.toLong(), limit, offset, ::summary)
                }
            }
        }

        return QueryPagingSource(
            countQuery = queries.countGalleriesInCollection(id.value),
            transacter = queries,
            context = dispatchers.IO,
            queryProvider = queryProvider,
        )
    }

    override fun getCollectionIds(id: GalleryId): Flow<List<GalleryCollectionId>> =
        queries.selectCollectionIdsForGallery(id.value)
            .asFlow()
            .mapToList(dispatchers.IO)
            .map { results -> results.map { GalleryCollectionId(it) } }

    override fun collectionName(id: GalleryCollectionId): Flow<String> =
        queries.selectCollectionName(id.value)
            .asFlow()
            .mapToOneNotNull(dispatchers.IO)

    override fun isFavorite(id: GalleryId): Flow<Boolean> =
        queries.isGalleryInFavoritesCollection(id.value)
            .asFlow()
            .mapToOneOrDefault(0, dispatchers.IO)
            .map { it != 0L }

    override suspend fun createCollection(name: String, createdAt: Instant): GalleryCollectionId = withContext(dispatchers.IO) {
        val id = queries.insertCollection(name, createdAt.epochSeconds).executeAsOne()
        GalleryCollectionId(id)
    }

    override suspend fun rename(id: GalleryCollectionId, name: String) {
        require(id.value != GalleryCollectionId.FAVORITES_ID) { "Cannot rename favorites collection." }
        withContext(dispatchers.IO) {
            queries.renameCollection(name, id.value)
        }
    }

    override suspend fun addGallery(
        galleryId: GalleryId,
        collections: List<GalleryCollectionId>,
        instant: Instant,
    ) {
        if (collections.isEmpty()) {
            return
        }

        withContext(dispatchers.IO) {
            collections.forEach { collectionId ->

                val entity = CollectionHasGallery(
                    collectionId = collectionId.value,
                    galleryId = galleryId.value,
                    createdAt = instant.epochSeconds
                )
                queries.addToCollection(entity)
                queries.setUpdatedAtCollection(updateAtInstant = instant.epochSeconds, id = collectionId.value)
            }
        }
    }

    override suspend fun removeGallery(
        galleryId: GalleryId,
        collections: List<GalleryCollectionId>,
        instant: Instant,
    ) {
        if (collections.isEmpty()) {
            return
        }

        withContext(dispatchers.IO) {
            database.transaction {
                collections.forEach { collectionId ->
                    queries.removeFromCollection(collectionId = collectionId.value, galleryId = galleryId.value)
                    queries.setUpdatedAtCollection(updateAtInstant = instant.epochSeconds, id = collectionId.value)
                }
            }
        }
    }

    override suspend fun addAndRemoveGallery(
        galleryId: GalleryId,
        added: List<GalleryCollectionId>,
        removed: List<GalleryCollectionId>,
        timestamp: Instant
    ) = withContext(dispatchers.IO) {
        database.transaction {
            added.forEach { collectionId ->

                val entity = CollectionHasGallery(
                    collectionId = collectionId.value,
                    galleryId = galleryId.value,
                    createdAt = timestamp.epochSeconds
                )

                queries.addToCollection(entity)
                queries.setUpdatedAtCollection(updateAtInstant = timestamp.epochSeconds, id = collectionId.value)
            }

            removed.forEach { collectionId ->
                queries.removeFromCollection(collectionId = collectionId.value, galleryId = galleryId.value)
                queries.setUpdatedAtCollection(updateAtInstant = timestamp.epochSeconds, id = collectionId.value)
            }
        }
    }

    override suspend fun deleteCollection(id: GalleryCollectionId) {
        require(id.value != GalleryCollectionId.FAVORITES_ID) { "Cannot delete favorites collection." }
        withContext(dispatchers.IO) {
            queries.deleteCollection(id.value)
        }
    }
}

private fun summary(
    id: Long,
    mediaId: Long,
    prettyTitle: String,
    coverThumbnailFileExtension: String?,
    isEnglish: Long?,
    isJapanese: Long?,
    isChinese: Long?,
): GallerySummary = GallerySummary(
    id = GalleryId(id),
    title = prettyTitle,
    language = when {
        isEnglish == 1L -> GalleryLanguage.English
        isJapanese == 1L -> GalleryLanguage.Japanese
        isChinese == 1L -> GalleryLanguage.Chinese
        else -> GalleryLanguage.Unknown
    },
    images = GallerySummaryImages.Remote(
        thumbnail = GalleryImage.Remote(
            NHentaiUrl.galleryCoverThumbnail(
                mediaId = MediaId(mediaId),
                fileType = coverThumbnailFileExtension
                    ?.let { GalleryImageFileType.fromFileExtension(it) }
                    ?: GalleryImageFileType.WEBP(hasWebpExtension = false)
            )
        )
    )
)

suspend fun GalleryCollectionRepository.favorite(id: GalleryId, instant: Instant) {
    addGallery(id, listOf(GalleryCollectionId(GalleryCollectionId.FAVORITES_ID)), instant)
}

suspend fun GalleryCollectionRepository.unfavorite(id: GalleryId, instant: Instant) {
    removeGallery(id, listOf(GalleryCollectionId(GalleryCollectionId.FAVORITES_ID)), instant)
}
