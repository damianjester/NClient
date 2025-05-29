package com.github.damianjester.nclient.ui.gallery.comments

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.Comment
import io.ktor.http.Url

@Composable
fun GalleryCommentsRootContent(
    component: CommentsComponent,
    modifier: Modifier = Modifier,
) {
    CommentsScreen(
        modifier = modifier,
        component = component,
        onBack = component::navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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

@Composable
fun CommentsList(
    modifier: Modifier,
    comments: List<Comment>,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(comments, key = { it.id.value }) { comment ->
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
    comment: Comment,
) {
    Card(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            PosterAvatar(
                url = comment.poster.avatar
            )
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
                    formatCommentPostDate(context, comment.elapsedTime),
                    modifier = Modifier.alpha(0.6f),
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
fun PosterAvatar(
    modifier: Modifier = Modifier,
    url: Url?
) {
    if (url != null) {
        var isError by remember { mutableStateOf(false) }

        if (isError) {
            BlankAvatar()
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url.toString())
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                onState = { state ->
                    if (state is AsyncImagePainter.State.Error) {
                        isError = true
                    }
                },
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .clip(CircleShape)
                    .size(64.dp),
            )
        }
    } else {
        BlankAvatar()
    }
}

@Composable
fun BlankAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(64.dp)
            .background(MaterialTheme.colorScheme.background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )
    }
}
