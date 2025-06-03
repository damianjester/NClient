package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.CommentEntity
import com.github.damianjester.nclient.CommentPosterEntity
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.net.models.Comment
import kotlinx.datetime.Clock

fun Comment.toCommentEntity(galleryId: GalleryId) =
    CommentEntity(
        id = id,
        galleryId = galleryId.value,
        posterId = poster.id,
        date = postDate,
        body = body,
        createdAt = Clock.System.now().epochSeconds
    )

fun Comment.toCommentPosterEntity() =
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
