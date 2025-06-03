package com.github.damianjester.nclient.db

import com.github.damianjester.nclient.CommentEntity
import com.github.damianjester.nclient.CommentPosterEntity
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.core.models.Comment
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.db.mappers.toComment
import com.github.damianjester.nclient.db.mappers.toCommentEntity
import com.github.damianjester.nclient.db.mappers.toCommentPosterEntity
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

interface CommentRepository {
    suspend fun selectComments(id: GalleryId): List<Comment>

    suspend fun selectCreatedAt(id: GalleryId): Instant?

    suspend fun replaceAllComments(id: GalleryId, response: List<com.github.damianjester.nclient.net.models.Comment>)
}

data class CommentEntityWithPosterEntity(
    val comment: CommentEntity,
    val poster: CommentPosterEntity,
)

class SqlDelightCommentRepository(
    private val database: Database,
    private val dispatchers: NClientDispatchers,
    private val logger: Logger,
) : CommentRepository {
    private val queries
        get() = database.commentEntityQueries

    override suspend fun selectComments(id: GalleryId) = withContext(dispatchers.IO) {
        queries.selectCommentsWithPosterEntity(id)
            .executeAsList()
            .map { it.toComment() }
    }

    override suspend fun selectCreatedAt(id: GalleryId) = withContext(dispatchers.IO) {
        queries.selectCreatedAt(id.value)
            .executeAsOneOrNull()
            ?.let { Instant.fromEpochSeconds(it) }
    }

    override suspend fun replaceAllComments(
        id: GalleryId,
        response: List<com.github.damianjester.nclient.net.models.Comment>
    ) = withContext(dispatchers.IO) {
        if (response.isEmpty()) {
            return@withContext
        }

        val posters = response
            .distinctBy { poster -> poster.id }
            .map { comment -> comment.toCommentPosterEntity() }

        val comments = response.map { it.toCommentEntity(id) }

        logger.i(
            LogTags.comments,
            "Inserting ${comments.size} comments for gallery #${comments.first().galleryId} and " +
                "${posters.size} posters."
        )

        database.transaction {
            queries.deleteComments(id.value)
            posters.forEach { queries.insertCommentPoster(it) }
            comments.forEach { queries.insertComment(it) }
        }
    }
}
