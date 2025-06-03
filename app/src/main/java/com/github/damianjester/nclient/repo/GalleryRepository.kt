package com.github.damianjester.nclient.repo

import com.github.damianjester.nclient.GalleryQueryEntity
import com.github.damianjester.nclient.core.models.GalleryDetails
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryPage
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.GalleryTitle
import com.github.damianjester.nclient.net.models.GallerySummariesResponse
import com.github.damianjester.nclient.net.models.GalleryDetailsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface GalleryRepository {
    suspend fun selectSummariesForQuery(query: GalleryQueryEntity): List<GallerySummary>

    suspend fun selectGalleryDetails(id: GalleryId): GalleryDetails

    suspend fun selectGalleryPages(id: GalleryId): List<GalleryPage>

    fun selectGalleryTitle(id: GalleryId): Flow<GalleryTitle>

    suspend fun selectGalleryUpdatedAt(id: GalleryId): Instant?

    suspend fun countPagesForGallery(id: GalleryId): Int

    suspend fun replaceAllGallerySummaries(query: GalleryQueryEntity, response: GallerySummariesResponse)

    suspend fun upsertGalleryDetails(response: GalleryDetailsResponse)
}

