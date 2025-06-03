package com.github.damianjester.nclient.ui.gallery.comments

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.Comment
import com.github.damianjester.nclient.ui.common.LoadingContent
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

    LaunchedEffectSnackbar(
        state = state.comments,
        snackbarHostState = snackbarHostState,
        onRetry = {
            component.retryComments()
        },
    )

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

        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (val state = state.comments) {
            is CommentsComponent.CommentsState.Loading -> LoadingContent(contentModifier)
            is CommentsComponent.CommentsState.Error -> ErrorContent(contentModifier, state)
            is CommentsComponent.CommentsState.Loaded -> {
                CommentsListContent(
                    modifier = contentModifier,
                    comments = state.comments,
                    isRefreshing = state.isRefreshing,
                    onRefresh = { component.refreshComments() }
                )
            }
        }
    }
}

@Composable
private fun LaunchedEffectSnackbar(
    state: CommentsComponent.CommentsState,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(state) {
        if (state is CommentsComponent.CommentsState.Error) {
            val actionLabel = when (state) {
                CommentsComponent.CommentsState.Error.Internal -> null
                CommentsComponent.CommentsState.Error.NetworkConnection ->
                    context.getString(R.string.retry)
            }

            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.comments_load_failed),
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed) {
                onRetry()
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
                MutableValue(
                    CommentsComponent.Model(
                        comments = CommentsComponent.CommentsState.Loaded(comments, refreshing = false)
                    )
                )

            override fun retryComments() = Unit

            override fun refreshComments() = Unit

            override fun navigateBack() = Unit

            override fun openCommentsWebpage() = Unit
        }

        CommentsScreen(
            component = component,
            onBack = {},
            onOpenCommentsWebpage = {}
        )
    }
}
