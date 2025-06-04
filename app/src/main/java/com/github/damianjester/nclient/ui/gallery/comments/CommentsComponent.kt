package com.github.damianjester.nclient.ui.gallery.comments

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnStart
import com.github.damianjester.nclient.core.CommentsFetcher
import com.github.damianjester.nclient.core.WebPageOpener
import com.github.damianjester.nclient.core.models.Comment
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.net.NHentaiClientConnectionException
import com.github.damianjester.nclient.net.NHentaiClientException
import com.github.damianjester.nclient.net.NHentaiClientSerializationException
import com.github.damianjester.nclient.net.NHentaiUrl
import com.github.damianjester.nclient.ui.gallery.comments.CommentsComponent.CommentsState
import com.github.damianjester.nclient.ui.gallery.comments.CommentsComponent.Model
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface CommentsComponent {
    val model: Value<Model>

    fun retryComments()

    fun refreshComments()

    fun navigateBack()

    fun openCommentsWebpage()

    data class Model(
        val comments: CommentsState = CommentsState.Loading,
    )

    sealed interface CommentsState {
        data object Loading : CommentsState

        data class Loaded(
            val comments: List<Comment>,
            val refreshing: Boolean,
        ) : CommentsState

        sealed interface Error : CommentsState {
            data object NetworkConnection : Error

            data object Internal : Error
        }

        val isLoading: Boolean
            get() = this is Loading

        val isRefreshing: Boolean
            get() = this is Loaded && refreshing
    }
}

class DefaultCommentsComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    private val galleryId: GalleryId,
    private val onNavigateBack: () -> Unit,
    private val fetcher: CommentsFetcher,
    private val webPageOpener: WebPageOpener,
) : CommentsComponent, ComponentContext by componentContext, KoinComponent {
    private val _model = MutableValue<Model>(Model())
    override val model: Value<Model> = _model

    private val coroutineScope = coroutineScope(dispatchers.Main.immediate)

    init {
        doOnStart(isOneTime = true) {
            fetchComments()
        }
    }

    override fun retryComments() {
        val currentState = _model.value.comments
        if (currentState.isLoading || currentState.isRefreshing) {
            return
        }

        fetchComments()
    }

    override fun refreshComments() {
        val currentState = _model.value.comments
        if (currentState.isLoading || currentState.isRefreshing) {
            return
        }
        doOnLoaded { loaded ->
            fetchComments(loaded)
        }
    }

    private fun fetchComments(loaded: CommentsState.Loaded? = null) {
        _model.update { state ->
            val loadingOrRefreshing = loaded?.copy(refreshing = true) ?: CommentsState.Loading
            state.copy(comments = loadingOrRefreshing)
        }

        coroutineScope.launch {
            try {
                val targetState = when (val result = fetcher.fetch(galleryId, loaded != null)) {
                    is Result.Err -> error(result.cause)
                    is Result.Ok -> CommentsState.Loaded(result.value, refreshing = false)
                }

                _model.update { state -> state.copy(comments = targetState) }
            } catch (ex: NHentaiClientException) {
                val targetState = when (ex) {
                    is NHentaiClientConnectionException -> CommentsState.Error.NetworkConnection
                    is NHentaiClientSerializationException -> CommentsState.Error.Internal
                    else -> throw ex
                }

                _model.update { state -> state.copy(comments = targetState) }
            }
        }
    }

    override fun navigateBack() = onNavigateBack()

    override fun openCommentsWebpage() {
        coroutineScope.launch {
            webPageOpener.open(NHentaiUrl.galleryWebPage(galleryId, commentsSection = true))
        }
    }

    private fun doOnLoaded(block: (state: CommentsState.Loaded) -> Unit) {
        val state = _model.value.comments
        if (state is CommentsState.Loaded) {
            block(state)
        }
    }
}
