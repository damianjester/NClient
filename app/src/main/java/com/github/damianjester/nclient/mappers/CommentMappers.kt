package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.core.models.Comment
import com.github.damianjester.nclient.core.models.CommentId
import com.github.damianjester.nclient.core.models.CommentPoster
import com.github.damianjester.nclient.core.models.UserId
import com.github.damianjester.nclient.db.models.CommentEntityWithPosterEntity
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
