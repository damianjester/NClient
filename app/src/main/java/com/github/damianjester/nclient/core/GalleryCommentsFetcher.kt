package com.github.damianjester.nclient.core

import android.util.Log
import com.github.damianjester.nclient.CommentEntity
import com.github.damianjester.nclient.CommentPosterEntity
import com.github.damianjester.nclient.GalleryId
import com.github.damianjester.nclient.core.GalleryCommentsFetcher.Result.Failure
import com.github.damianjester.nclient.core.GalleryCommentsFetcher.Result.Success
import com.github.damianjester.nclient.db.CommentRepository
import com.github.damianjester.nclient.net.CommentResponse
import com.github.damianjester.nclient.net.NHentaiHttpClient

interface GalleryCommentsFetcher {
    suspend fun fetch(id: GalleryId): Result
    sealed interface Result {
        data object Success : Result
        sealed interface Failure : Result {
            data class Network(val exception: Exception) : Failure
            data class Database(val exception: Exception) : Failure
        }
    }
}

class DefaultGalleryCommentsFetcher(
    private val client: NHentaiHttpClient,
    private val repository: CommentRepository,
) : GalleryCommentsFetcher {
    override suspend fun fetch(id: GalleryId): GalleryCommentsFetcher.Result {
        val response: List<CommentResponse>

        try {
            response = client.getComments(id)
        } catch (ex: Exception) {
            Log.e("comments", "Failed to fetch comments for $id.", ex)
            return Failure.Network(ex)
        }

        val posters = response
            .map { comment ->
                CommentPosterEntity(
                    id = comment.poster.id,
                    username = comment.poster.username,
                    avatarPath = comment.poster.avatarUrl
                )
            }
            .distinctBy { poster -> poster.id }

        val comments = response
            .map { comment ->
                CommentEntity(
                    id = comment.id,
                    galleryId = id.value,
                    posterId = comment.poster.id,
                    date = comment.postDate,
                    body = comment.body
                )
            }

        try {
            repository.insert(comments, posters)
        } catch (ex: Exception) {
            Log.e("comments", "Failed to insert comments for $id.", ex)
            return Failure.Database(ex)
        }

        return Success
    }
}
