package com.github.damianjester.nclient.db.mappers

import com.github.damianjester.nclient.CommentEntity
import com.github.damianjester.nclient.CommentPosterEntity
import com.github.damianjester.nclient.core.Comment
import com.github.damianjester.nclient.core.CommentId
import com.github.damianjester.nclient.core.CommentPoster
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.UserId
import com.github.damianjester.nclient.db.CommentEntityWithPosterEntity
import com.github.damianjester.nclient.net.CommentResponse
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun CommentEntityWithPosterEntity.toComment(): Comment {
    val now = Clock.System.now()
    val instant = Instant.fromEpochSeconds(comment.date)

    return Comment(
        id = CommentId(comment.id),
        poster = CommentPoster(
            id = UserId(poster.id),
            username = poster.username,
            avatar = poster.avatarPath?.let { NHentaiUrl.posterAvatar(it) }
        ),
        date = instant.toLocalDateTime(TimeZone.UTC),
        elapsedTime = now.minus(instant),
        body = comment.body
    )
}

fun CommentResponse.toCommentEntity(galleryId: GalleryId) =
    CommentEntity(
        id = id,
        galleryId = galleryId.value,
        posterId = poster.id,
        date = postDate,
        body = body,
        createdAt = Clock.System.now().epochSeconds
    )

fun CommentResponse.toCommentPosterEntity() =
    CommentPosterEntity(
        id = poster.id,
        username = poster.username,
        avatarPath = removeQueryFromAvatarPath(poster.avatarUrl)
    )

private fun removeQueryFromAvatarPath(path: String): String {
    // avatars/7433161.png?_=01e6e2e517774b70
    //        Remove from ^ here         to ^ here (if it is present)

    val indexOfParameters = path.indexOf('?')

    return if (indexOfParameters != -1) {
        path.substring(0, indexOfParameters)
    } else {
        path
    }
}
