package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.GalleryDetailsCacher.CacheResult
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.models.GalleryDetailsResponse
import com.github.damianjester.nclient.net.NHentaiHttpClient
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

interface GalleryDetailsCacher {
    suspend fun cache(id: GalleryId): Result<CacheResult, NClientError>

    sealed interface CacheResult {
        data object CacheValid : CacheResult

        data object Fetched : CacheResult
    }
}

class DefaultGalleryDetailsCacher(
    private val logger: Logger,
    private val client: NHentaiHttpClient,
    private val repository: GalleryRepository,
) : GalleryDetailsCacher {
    override suspend fun cache(id: GalleryId): Result<CacheResult, NClientError> {
        val updatedAt = repository.selectGalleryUpdatedAt(id)
        if (updatedAt != null && Clock.System.now() - updatedAt <= 7.days) {
            logger.i(
                LogTags.gallery,
                "Gallery details ($id) from local cache still valid (last update: ${Clock.System.now() - updatedAt})."
            )
            return Result.Ok(CacheResult.CacheValid)
        }

        val response: GalleryDetailsResponse = when (val result = client.getGalleryDetails(id)) {
            is Result.Err -> return Result.Err(result.cause)
            is Result.Ok -> result.value
        }

        repository.upsertGalleryDetails(response)
        return Result.Ok(CacheResult.Fetched)
    }
}
