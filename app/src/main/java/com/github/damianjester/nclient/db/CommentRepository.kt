package com.github.damianjester.nclient.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.damianjester.nclient.CommentEntity
import com.github.damianjester.nclient.CommentPosterEntity
import com.github.damianjester.nclient.Database
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.utils.LogTags
import com.github.damianjester.nclient.utils.Logger
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface CommentRepository {
    fun selectComments(id: GalleryId): Flow<List<CommentEntityWithPosterEntity>>

    suspend fun insert(
        comments: List<CommentEntity>,
        posters: List<CommentPosterEntity>,
    )
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

    override fun selectComments(id: GalleryId): Flow<List<CommentEntityWithPosterEntity>> {
        // gc.id, gc.posterId, gc.date, gc.body, gp.username, gp.avatarPath
        return queries.selectCommentsForGallery(id.value) { commentId, posterId, date, body, username, avatarPath ->
            CommentEntityWithPosterEntity(
                comment = CommentEntity(
                    id = commentId,
                    galleryId = id.value,
                    posterId = posterId,
                    date = date,
                    body = body
                ),
                poster = CommentPosterEntity(
                    id = posterId,
                    username = username,
                    avatarPath = avatarPath
                )
            )
        }
            .asFlow()
            .mapToList(dispatchers.IO)
    }

    override suspend fun insert(
        comments: List<CommentEntity>,
        posters: List<CommentPosterEntity>,
    ) = withContext(dispatchers.IO) {
        // TODO: Check size

        logger.i(
            LogTags.comments,
            "Inserting ${comments.size} comments for gallery #${comments.first().galleryId} and " +
                "${posters.size} posters."
        )

        database.transaction {
            posters.forEach { queries.insertCommentPoster(it) }
            comments.forEach { queries.insertComment(it) }
        }
    }
}
