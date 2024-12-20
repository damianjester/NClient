package com.github.damianjester.nclient.gallery.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentsScreen(
    modifier: Modifier = Modifier,
    component: CommentsComponent,
    onBack: () -> Unit,
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
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->

        val pullToRefreshState = rememberPullRefreshState(
            refreshing = state.isRefreshing,
            onRefresh = { component.loadComments(pullToRefresh = true) }
        )

        Box(
            modifier = Modifier
                .pullRefresh(pullToRefreshState)
                .padding(innerPadding)
        ) {

            if (state.isLoading && !state.isRefreshing) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            } else {
                if (state.comments.isEmpty()) {
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

            PullRefreshIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                refreshing = state.isRefreshing,
                state = pullToRefreshState
            )
        }
    }
}

@Composable
fun CommentsList(
    modifier: Modifier,
    comments: List<CommentsComponent.Comment>,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(comments, key = { it.id }) { comment ->
            CommentItem(
                modifier = Modifier.fillMaxWidth(),
                comment = comment
            )
        }
    }
}

@Composable
fun CommentItem(
    modifier: Modifier = Modifier,
    comment: CommentsComponent.Comment,
) {
    Card(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            if (comment.poster.avatarUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(comment.poster.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(64.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colors.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            Spacer(Modifier.size(16.dp))
            Column {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = comment.poster.username,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(8.dp))
                SelectionContainer {
                    Text(comment.body)
                }
                Spacer(Modifier.size(8.dp))

                val context = LocalContext.current
                Text(
                    formatCommentPostDate(context, comment.postedDuration),
                    modifier = Modifier.alpha(0.6f),
                    fontSize = 14.sp,
                )
            }
        }
    }
}
