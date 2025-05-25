package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.CommentId
import com.github.damianjester.nclient.CommentPoster
import com.github.damianjester.nclient.Comment
import com.github.damianjester.nclient.GalleryId
import com.github.damianjester.nclient.NClientDispatchers
import com.github.damianjester.nclient.net.NHentaiUrl
import com.github.damianjester.nclient.UserId
import com.github.damianjester.nclient.db.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface GalleryCommentsObserver {
    fun comments(id: GalleryId): Flow<List<Comment>>
}

class DefaultGalleryCommentsObserver(
    private val repository: CommentRepository,
    private val dispatchers: NClientDispatchers,
) : GalleryCommentsObserver {

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
