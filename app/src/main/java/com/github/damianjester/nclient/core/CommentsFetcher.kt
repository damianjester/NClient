package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.CommentEntity
import com.github.damianjester.nclient.CommentPosterEntity
import com.github.damianjester.nclient.core.CommentsFetcher.Result.Failure
import com.github.damianjester.nclient.core.CommentsFetcher.Result.Success
import com.github.damianjester.nclient.db.CommentRepository
import com.github.damianjester.nclient.net.CommentResponse
import com.github.damianjester.nclient.net.NHentaiHttpClient
import com.github.damianjester.nclient.utils.Logger
import com.github.damianjester.nclient.utils.LogTags

interface CommentsFetcher {
    suspend fun fetch(id: GalleryId): Result

    sealed interface Result {
        data object Success : Result

        sealed interface Failure : Result {
            data class Network(val exception: Exception) : Failure

            data class Database(val exception: Exception) : Failure
        }
    }
}

class DefaultCommentsFetcher(
    private val client: NHentaiHttpClient,
    private val repository: CommentRepository,
    private val logger: Logger,
) : CommentsFetcher {
    override suspend fun fetch(id: GalleryId): CommentsFetcher.Result {
        val response: List<CommentResponse>

        try {
            response = client.getComments(id)
        } catch (ex: Exception) {
            logger.e(LogTags.comments, "Failed to fetch comments for $id.", ex)
            return Failure.Network(ex)
        }

        val posters = response
            .map { comment ->
                CommentPosterEntity(
                    id = comment.poster.id,
                    username = comment.poster.username,
                    avatarPath = removeQueryFromAvatarPath(comment.poster.avatarUrl)
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
            logger.e(LogTags.comments, "Failed to insert comments for $id.", ex)
            return Failure.Database(ex)
        }

        return Success
    }

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
}
