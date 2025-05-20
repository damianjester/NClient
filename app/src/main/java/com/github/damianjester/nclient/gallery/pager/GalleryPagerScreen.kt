package com.github.damianjester.nclient.gallery.pager

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areSystemBarsVisible
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GalleryPagerRootContent(
    component: GalleryPagerComponent,
    modifier: Modifier = Modifier
) {
    GalleryPagerScreen(
        modifier = modifier,
        component = component,
        initialPage = component.config.pageIndex ?: 0,
        onBack = { component.navigateBack() },
        onDownloadPage = { page -> component.savePageToGallery(page) },
        onSharePage = { page, includeUrl -> component.sharePage(page, includeUrl) },
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun GalleryPagerScreen(
    modifier: Modifier,
    component: GalleryPagerComponent,
    initialPage: Int = 0,
    onBack: () -> Unit,
    onDownloadPage: (GalleryPagerComponent.GalleryPage) -> Unit,
    onSharePage: (GalleryPagerComponent.GalleryPage, Boolean) -> Unit,
) {
    val state by component.model.subscribeAsState()
    val galleryState = state.gallery

    val context = LocalContext.current
    val windowInsetsController = rememberWindowInsetsController()
    val snackbarHostState = remember { SnackbarHostState() }

    val areSystemBarsVisible = WindowInsets.areSystemBarsVisible
    var uiVisible by remember { mutableStateOf(true) }
    var jumpToPageDialogVisible by remember { mutableStateOf(false) }
    var shareDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(areSystemBarsVisible) {
        if (areSystemBarsVisible) {
            uiVisible = true
        }
    }

    LaunchedEffect(uiVisible) {
        windowInsetsController.setSystemBarsVisibility(uiVisible)
    }

    LaunchedEffect(component.snackbarMessage) {
        component.snackbarMessage.collectLatest { message ->
            when (message) {
                GalleryPagerComponent.SnackbarMessage.PageDownloaded ->
                    snackbarHostState.showSnackbar(context.getString(R.string.gallery_page_saved))

                GalleryPagerComponent.SnackbarMessage.PageDownloadFailed ->
                    snackbarHostState.showSnackbar(context.getString(R.string.gallery_page_save_failed))

                GalleryPagerComponent.SnackbarMessage.PageShareFailed ->
                    snackbarHostState.showSnackbar(context.getString(R.string.gallery_page_share_failed))
            }
        }
    }

    val galleryTitle = when (galleryState) {
        is GalleryPagerComponent.GalleryState.Loaded -> galleryState.gallery.title
        else -> null
    }

    val pagerState = rememberPagerState(initialPage = initialPage) {
        if (galleryState is GalleryPagerComponent.GalleryState.Loaded) {
            galleryState.gallery.pages.size
        } else {
            0
        }
    }

    GalleryPagerScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                galleryTitle = galleryTitle,
                visible = uiVisible,
                onBack = onBack,
                onDownloadPage = {
                    val page =
                        (if (galleryState is GalleryPagerComponent.GalleryState.Loaded) galleryState else null)
                            ?.let { it.gallery.pages[pagerState.currentPage] }
                            ?: return@TopAppBar
                    onDownloadPage(page)
                },
                onSharePage = {
                    shareDialogVisible = true
                }
            )
        },
        bottomBar = {
            GalleryPagerBottomBar(
                visible = uiVisible,
                pagerState = pagerState,
                onJumpToPage = { jumpToPageDialogVisible = true }
            )
        }
    ) { innerPadding ->

        if (galleryState is GalleryPagerComponent.GalleryState.Loaded) {
            GalleryPager(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                pagerState = pagerState,
                pages = galleryState.gallery.pages,
                onPageClick = {
                    uiVisible = !uiVisible
                }
            )
        }

        if (jumpToPageDialogVisible) {
            JumpToPageDialog(
                onDismissRequest = { jumpToPageDialogVisible = false },
                pagerState = pagerState,
            )
        }

        if (shareDialogVisible) {
            SharePageDialog(
                onDismissRequest = { shareDialogVisible = false },
                onShare = { includeUrl ->
                    val page =
                        (if (galleryState is GalleryPagerComponent.GalleryState.Loaded) galleryState else null)
                            ?.let { it.gallery.pages[pagerState.currentPage] }
                            ?: return@SharePageDialog
                    onSharePage(page, includeUrl)
                    shareDialogVisible = false
                }
            )
        }
    }
}
