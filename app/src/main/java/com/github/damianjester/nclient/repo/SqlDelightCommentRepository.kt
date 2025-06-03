package com.github.damianjester.nclient.repo

import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.db.mappers.toComment
import com.github.damianjester.nclient.db.mappers.toCommentEntity
import com.github.damianjester.nclient.db.mappers.toCommentPosterEntity
import com.github.damianjester.nclient.db.selectCommentsWithPosterEntity
import com.github.damianjester.nclient.net.models.CommentsResponse
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

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
            ?.let { Instant.Companion.fromEpochSeconds(it) }
    }

    override suspend fun replaceAllComments(
        id: GalleryId,
        response: CommentsResponse
    ) = withContext(dispatchers.IO) {
        if (response.comments.isEmpty()) {
            return@withContext
        }

        val posters = response.comments
            .distinctBy { poster -> poster.id }
            .map { comment -> comment.toCommentPosterEntity() }

        val comments = response.comments.map { it.toCommentEntity(id) }

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
