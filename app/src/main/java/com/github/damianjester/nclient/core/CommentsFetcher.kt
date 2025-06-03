package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.Comment
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.db.CommentRepository
import com.github.damianjester.nclient.net.NHentaiHttpClient
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

interface CommentsFetcher {
    suspend fun fetch(id: GalleryId, refresh: Boolean): Result<List<Comment>, NClientError>
}

class DefaultCommentsFetcher(
    private val logger: Logger,
    private val client: NHentaiHttpClient,
    private val repository: CommentRepository,
) : CommentsFetcher {
    override suspend fun fetch(id: GalleryId, refresh: Boolean): Result<List<Comment>, NClientError> {
        if (!refresh) {
            val now = Clock.System.now()
            val createdAt = repository.selectCreatedAt(id)

            // Cache comments for one hour
            if (createdAt != null && now - createdAt <= 1.hours) {
                logger.i(
                    LogTags.comments,
                    "Comments for gallery ($id) from local cache still valid (last update: ${now - createdAt})."
                )
                return Result.Ok(repository.selectComments(id))
            }
        }

        val response = client.getComments(id)
        repository.replaceAllComments(id, response)

        return Result.Ok(repository.selectComments(id))
    }
}
