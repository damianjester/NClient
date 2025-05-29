package com.github.damianjester.nclient.ui.gallery.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.Comment
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun CommentsRootContent(
    component: CommentsComponent,
    modifier: Modifier = Modifier,
) {
    CommentsScreen(
        modifier = modifier,
        component = component,
        onBack = component::navigateBack,
        onOpenCommentsWebpage = component::openCommentsWebpage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    modifier: Modifier = Modifier,
    component: CommentsComponent,
    onBack: () -> Unit,
    onOpenCommentsWebpage: () -> Unit,
) {
    val state by component.model.subscribeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.commentsState) {
        if (state.commentsState is CommentsComponent.CommentsState.Error) {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.comments_load_failed),
                actionLabel = context.getString(R.string.retry),
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed) {
                component.loadComments(pullToRefresh = false)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.comments)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenCommentsWebpage() }) {
                        Icon(
                            Icons.Default.OpenInBrowser,
                            contentDescription = stringResource(R.string.open_in_browser)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        PullToRefreshBox(
            state.isRefreshing,
            onRefresh = { component.loadComments(pullToRefresh = true) },
            modifier = Modifier.padding(innerPadding)
        ) {
            if (state.isLoading && !state.isRefreshing) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            } else {
                if (!state.isLoading && state.comments.isEmpty()) {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            stringResource(R.string.no_comments),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    CommentsList(
                        modifier = Modifier.fillMaxSize(),
                        comments = state.comments
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CommentsScreenPreview(
    @PreviewParameter(CommentsPreviewParameterProvider::class) comments: List<Comment>,
) {
    NClientPreviewTheme {
        val component = object : CommentsComponent {
            override val model: Value<CommentsComponent.Model> =
                MutableValue(CommentsComponent.Model(
                    commentsState = CommentsComponent.CommentsState.Loaded,
                    comments = comments
                ))

            override fun loadComments(pullToRefresh: Boolean) {
            }

            override fun navigateBack() {
            }

            override fun openCommentsWebpage() {
            }
        }

        CommentsScreen(
            component = component,
            onBack = {},
            onOpenCommentsWebpage = {}
        )
    }
}
