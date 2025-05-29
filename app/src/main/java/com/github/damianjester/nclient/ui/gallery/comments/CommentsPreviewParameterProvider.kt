package com.github.damianjester.nclient.ui.gallery.comments

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.github.damianjester.nclient.core.Comment
import com.github.damianjester.nclient.core.CommentId
import com.github.damianjester.nclient.core.CommentPoster
import com.github.damianjester.nclient.core.UserId
import io.ktor.http.Url
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CommentsPreviewParameterProvider : PreviewParameterProvider<List<Comment>> {
    override val values: Sequence<List<Comment>>
        get() = sequenceOf(
            listOf(
                Comment(
                    id = CommentId(1),
                    poster = CommentPoster(
                        id = UserId(1234),
                        username = "SmoothBrain",
                        avatar = null
                    ),
                    date = LocalDateTime(2025, 1, 1, 15, 30, 0, 0),
                    elapsedTime = (5.minutes + 20.seconds),
                    body = "Hello, World!"
                ),
                Comment(
                    id = CommentId(2),
                    poster = CommentPoster(
                        id = UserId(2842),
                        username = "AverageFeetEnjoyer",
                        avatar = Url("https://example.com")
                    ),
                    date = LocalDateTime(2025, 1, 1, 15, 30, 0, 0),
                    elapsedTime = (2.hours + 52.minutes),
                    body = "keystrokes..."
                ),
                Comment(
                    id = CommentId(3),
                    poster = CommentPoster(
                        id = UserId(9421),
                        username = "EliteGoonerGeneral",
                        avatar = null
                    ),
                    date = LocalDateTime(2025, 1, 1, 15, 30, 0, 0),
                    elapsedTime = (28.hours),
                    body = "i wish that was me"
                ),
            )
        )
}
