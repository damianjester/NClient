package com.github.damianjester.nclient.ui.gallery.pager

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy

@Composable
fun GalleryPageScaffoldLayout(
    topBar: @Composable @UiComposable () -> Unit,
    bottomBar: @Composable @UiComposable () -> Unit,
    snackbar: @Composable @UiComposable () -> Unit,
    contentWindowInsets: WindowInsets,
    content: @Composable @UiComposable (PaddingValues) -> Unit,
) {
    SubcomposeLayout { constraints ->

        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val topBarPlaceables = subcompose(ScaffoldLayoutContent.TopBar, topBar).fastMap {
            it.measure(looseConstraints)
        }

        val snackbarPlaceables = subcompose(ScaffoldLayoutContent.Snackbar, snackbar).fastMap {
            // respect only bottom and horizontal for snackbar and fab
            val leftInset = contentWindowInsets
                .getLeft(this@SubcomposeLayout, layoutDirection)
            val rightInset = contentWindowInsets
                .getRight(this@SubcomposeLayout, layoutDirection)
            val bottomInset = contentWindowInsets.getBottom(this@SubcomposeLayout)
            // offset the snackbar constraints by the insets values
            it.measure(
                looseConstraints.offset(
                    -leftInset - rightInset,
                    -bottomInset
                )
            )
        }

        val snackbarHeight = snackbarPlaceables.fastMaxBy { it.height }?.height ?: 0

        val bottomBarPlaceables = subcompose(ScaffoldLayoutContent.BottomBar) {
            bottomBar()
        }.fastMap { it.measure(looseConstraints) }

        val bottomBarHeight = bottomBarPlaceables.fastMaxBy { it.height }?.height

        val snackbarOffsetFromBottom = if (snackbarHeight != 0) {
            snackbarHeight + (bottomBarHeight
                ?: contentWindowInsets.getBottom(this@SubcomposeLayout))
        } else {
            0
        }

        val bodyContentPlaceables = subcompose(ScaffoldLayoutContent.MainContent) {
            val insets = contentWindowInsets.asPaddingValues(this@SubcomposeLayout)
            val innerPadding = PaddingValues(
                top = 0.dp,
                bottom = 0.dp,
                start = insets.calculateStartPadding((this@SubcomposeLayout).layoutDirection),
                end = insets.calculateEndPadding((this@SubcomposeLayout).layoutDirection)
            )
            content(innerPadding)
        }.fastMap { it.measure(looseConstraints.copy(maxHeight = layoutHeight)) }

        layout(layoutWidth, layoutHeight) {
            // Placing to control drawing order to match default elevation of each placeable
            bodyContentPlaceables.fastForEach {
                it.place(0, 0)
            }
            topBarPlaceables.fastForEach {
                it.place(0, 0)
            }
            snackbarPlaceables.fastForEach {
                it.place(0, layoutHeight - snackbarOffsetFromBottom)
            }
            // The bottom bar is always at the bottom of the layout
            bottomBarPlaceables.fastForEach {
                it.place(0, layoutHeight - (bottomBarHeight ?: 0))
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun GalleryPagerScaffold(
    contentWindowInsets: WindowInsets = WindowInsets(0),
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    topBar: @Composable @UiComposable () -> Unit,
    bottomBar: @Composable @UiComposable () -> Unit,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable @UiComposable (PaddingValues) -> Unit,
) {

    val safeInsets = remember(contentWindowInsets) {
        MutableWindowInsets(contentWindowInsets)
    }

    Surface(
        modifier = modifier
            .onConsumedWindowInsetsChanged { consumedWindowInsets ->
                // Exclude currently consumed window insets from user provided contentWindowInsets
                safeInsets.insets = contentWindowInsets.exclude(consumedWindowInsets)
            },
        color = backgroundColor,
        contentColor = contentColor
    ) {
        GalleryPageScaffoldLayout(
            topBar = topBar,
            content = content,
            contentWindowInsets = safeInsets,
            snackbar = {
                snackbarHost(snackbarHostState)
            },
            bottomBar = bottomBar
        )
    }
}

private enum class ScaffoldLayoutContent { TopBar, MainContent, Snackbar, BottomBar }
