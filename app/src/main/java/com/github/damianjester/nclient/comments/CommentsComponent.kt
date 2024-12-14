package com.github.damianjester.nclient.comments

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.github.damianjester.nclient.NHentaiUrl
import com.github.damianjester.nclient.coroutineScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.time.Duration

interface CommentsComponent {
    val model: Value<Model>

    fun loadComments(pullToRefresh: Boolean)

    data class Model(
        val commentsState: CommentsState = CommentsState.Loading(false),
        val comments: List<Comment> = emptyList(),
    ) {
        val isLoading: Boolean
            get() = commentsState is CommentsState.Loading

        val isRefreshing: Boolean
            get() = commentsState is CommentsState.Loading && commentsState.userRefresh
    }

    sealed interface CommentsState {
        data class Loading(val userRefresh: Boolean) : CommentsState
        data class Error(val exception: Exception) : CommentsState
        data object Loaded : CommentsState
    }

    data class Comment(
        val id: Long,
        val poster: User,
        val postedDuration: Duration,
        val body: String,
    )

    data class User(
        val username: String,
        val avatarUrl: String?,
    )
}

class DefaultCommentsComponent(
    componentContext: ComponentContext,
    private val galleryId: Long,
) : CommentsComponent, ComponentContext by componentContext {

    override val model = MutableValue(CommentsComponent.Model())
    private val scope = coroutineScope(Dispatchers.IO)

    private val httpClient = HttpClient(CIO.create()) {
        install(ContentNegotiation) {
            json(
                json = Json { ignoreUnknownKeys = true }
            )
        }
    }

    init {
        componentContext.doOnCreate {
            loadComments(pullToRefresh = false)
        }
        componentContext.doOnDestroy {
            httpClient.close()
        }
    }

    override fun loadComments(pullToRefresh: Boolean) {
        model.update {
            it.copy(
                commentsState = CommentsComponent.CommentsState.Loading(pullToRefresh),
            )
        }
        scope.launch {
            try {
                val response = httpClient.get(NHentaiUrl.commentsUrl(galleryId))
                val now = Clock.System.now()
                val comments = response.body<List<CommentResponse>>()
                    .map { it.toStateModel(now) }

                model.update { model ->
                    model.copy(
                        commentsState = CommentsComponent.CommentsState.Loaded,
                        comments = comments,
                    )
                }
            } catch (ex: Exception) {
                model.update { model ->
                    model.copy(
                        commentsState = CommentsComponent.CommentsState.Error(ex),
                    )
                }
            }
        }
    }

    private fun CommentResponse.toStateModel(now: Instant): CommentsComponent.Comment {
        return CommentsComponent.Comment(
            id = id,
            poster = poster.toStateModel(),
            postedDuration = now.minus(Instant.fromEpochSeconds(postDate)),
            body = body.trim()
        )
    }

    private fun PosterResponse.toStateModel(): CommentsComponent.User {
        return CommentsComponent.User(
            username = username,
            avatarUrl = NHentaiUrl.avatarUrl(avatarUrl) // avatarUrl is actually the URL path
        )
    }

}
