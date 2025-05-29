package com.github.damianjester.nclient.ui.gallery.comments

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.github.damianjester.nclient.core.Comment
import com.github.damianjester.nclient.core.CommentsFetcher
import com.github.damianjester.nclient.core.DefaultCommentsObserver
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.WebPageOpener
import com.github.damianjester.nclient.net.NHentaiUrl
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface CommentsComponent {
    val model: Value<Model>

    fun loadComments(pullToRefresh: Boolean)

    fun navigateBack()

    fun openCommentsWebpage()

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

        data object Error : CommentsState

        data object Loaded : CommentsState
    }
}

class DefaultCommentsComponent(
    componentContext: ComponentContext,
    private val galleryId: GalleryId,
    private val onNavigateBack: () -> Unit,
    private val fetcher: CommentsFetcher,
    private val observer: DefaultCommentsObserver,
    private val webPageOpener: WebPageOpener,
) : CommentsComponent, ComponentContext by componentContext, KoinComponent {
    private val _model = MutableValue(CommentsComponent.Model())
    private val coroutineScope = coroutineScope(Dispatchers.IO)

    override val model: Value<CommentsComponent.Model> = _model

    init {
        doOnCreate {
            coroutineScope.launch {
                val state = when (fetcher.fetch(galleryId)) {
                    CommentsFetcher.Result.Success -> CommentsComponent.CommentsState.Loaded
                    is CommentsFetcher.Result.Failure -> CommentsComponent.CommentsState.Error
                }
                _model.update { it.copy(commentsState = state) }
            }

            coroutineScope.launch {
                observer.comments(galleryId)
                    .collect { comments ->
                        _model.update {
                            it.copy(
                                comments = comments
                            )
                        }
                    }
            }
        }
    }

    override fun loadComments(pullToRefresh: Boolean) {
        _model.update { it.copy(CommentsComponent.CommentsState.Loading(pullToRefresh)) }
        coroutineScope.launch {
            val state = when (fetcher.fetch(galleryId)) {
                CommentsFetcher.Result.Success -> CommentsComponent.CommentsState.Loaded
                is CommentsFetcher.Result.Failure -> CommentsComponent.CommentsState.Error
            }

            _model.update { it.copy(state) }
        }
    }

    override fun navigateBack() = onNavigateBack()

    override fun openCommentsWebpage() {
        coroutineScope.launch {
            webPageOpener.open(NHentaiUrl.galleryWebPage(galleryId, commentsSection = true))
        }
    }
}
