package com.github.damianjester.nclient.gallery.comments

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.github.damianjester.nclient.Comment
import com.github.damianjester.nclient.GalleryId
import com.github.damianjester.nclient.core.DefaultGalleryCommentsObserver
import com.github.damianjester.nclient.core.GalleryCommentsFetcher
import com.github.damianjester.nclient.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface CommentsComponent {
    val model: Value<Model>

    fun loadComments(pullToRefresh: Boolean)
    fun navigateBack()

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
    private val fetcher: GalleryCommentsFetcher,
    private val observer: DefaultGalleryCommentsObserver,
) : CommentsComponent, ComponentContext by componentContext, KoinComponent {

    override val model = MutableValue(CommentsComponent.Model())
    private val coroutineScope = coroutineScope(Dispatchers.IO)

    init {
        componentContext.doOnCreate {
            loadComments(pullToRefresh = false)

            coroutineScope.launch {
                observer.comments(galleryId)
                    .collect { comments ->
                        model.update { it.copy(comments = comments) }
                    }
            }
        }
    }

    override fun loadComments(pullToRefresh: Boolean) {
        model.update { it.copy(CommentsComponent.CommentsState.Loading(pullToRefresh)) }
        coroutineScope.launch {

            val state = when (fetcher.fetch(galleryId)) {
                GalleryCommentsFetcher.Result.Success -> CommentsComponent.CommentsState.Loaded
                is GalleryCommentsFetcher.Result.Failure -> CommentsComponent.CommentsState.Error
            }

            model.update { it.copy(state) }
        }
    }

    override fun navigateBack() {
        onNavigateBack()
    }

}
