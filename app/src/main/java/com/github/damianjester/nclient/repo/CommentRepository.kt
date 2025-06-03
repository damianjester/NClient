package com.github.damianjester.nclient.repo

import com.github.damianjester.nclient.core.models.Comment
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.net.models.CommentsResponse
import kotlinx.datetime.Instant

interface CommentRepository {
    suspend fun selectComments(id: GalleryId): List<Comment>

    suspend fun selectCreatedAt(id: GalleryId): Instant?

    suspend fun replaceAllComments(id: GalleryId, response: CommentsResponse)
}
