package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.db.CommentRepository
import com.github.damianjester.nclient.net.NHentaiUrl
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface CommentsObserver {
    fun comments(id: GalleryId): Flow<List<Comment>>
}

class DefaultCommentsObserver(
    private val repository: CommentRepository,
    private val dispatchers: NClientDispatchers,
) : CommentsObserver {
    override fun comments(id: GalleryId): Flow<List<Comment>> {
        return repository.selectComments(id)
            .flowOn(dispatchers.Default)
            .map { comments ->

                val now = Clock.System.now()

                comments.map { (comment, poster) ->
                    val instant = Instant.fromEpochSeconds(comment.date)

                    Comment(
                        id = CommentId(comment.id),
                        poster = CommentPoster(
                            id = UserId(poster.id),
                            username = poster.username,
                            avatar = poster.avatarPath?.let { NHentaiUrl.avatarUrl(it) }
                        ),
                        date = instant.toLocalDateTime(TimeZone.UTC),
                        elapsedTime = now.minus(instant),
                        body = comment.body
                    )
                }
            }
    }
}
