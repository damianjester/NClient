package com.github.damianjester.nclient.core.models

import io.ktor.http.Url
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

@JvmInline
value class CommentId(val value: Long)

@JvmInline
value class UserId(val value: Long)

data class Comment(
    val id: CommentId,
    val poster: CommentPoster,
    val date: LocalDateTime,
    val elapsedTime: Duration,
    val body: String,
)

data class CommentPoster(
    val id: UserId,
    val username: String,
    val avatar: Url?,
)
