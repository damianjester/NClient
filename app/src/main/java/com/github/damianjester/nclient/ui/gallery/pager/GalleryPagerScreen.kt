package com.github.damianjester.nclient.ui.gallery.pager

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areSystemBarsVisible
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.GalleryPage
import com.github.damianjester.nclient.core.ScreenCaffeinater
import com.github.damianjester.nclient.ui.common.LoadingContent
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun GalleryPagerRootContent(
    component: GalleryPagerComponent,
    modifier: Modifier = Modifier,
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
    onDownloadPage: (GalleryPage) -> Unit,
    onSharePage: (GalleryPage, Boolean) -> Unit,
) {
    val state by component.model.subscribeAsState()
    val pagesState = state.pages

    val context = LocalContext.current
    val windowInsetsController = rememberWindowInsetsController()
    val snackbarHostState = remember { SnackbarHostState() }

    val areSystemBarsVisible = WindowInsets.areSystemBarsVisible
    var uiVisible by remember { mutableStateOf(true) }
    var jumpToPageDialogVisible by remember { mutableStateOf(false) }
    var shareDialogVisible by remember { mutableStateOf(false) }

    val screenCaffeinater = koinInject<ScreenCaffeinater>()

    LaunchedEffect(areSystemBarsVisible) {
        if (areSystemBarsVisible) {
            uiVisible = true
        }
    }

    DisposableEffect(uiVisible) {
        windowInsetsController.setSystemBarsVisibility(uiVisible)
        onDispose {
            windowInsetsController.setSystemBarsVisibility(true)
        }
    }

    DisposableEffect(Unit) {
        screenCaffeinater.caffinateScreen(true)
        onDispose { screenCaffeinater.caffinateScreen(false) }
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

    val pagerState = rememberPagerState(initialPage = initialPage) {
        pagesState.pagesOrNull?.size ?: 0
    }

    GalleryPagerScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                galleryTitle = state.galleryTitle?.pretty ?: "",
                visible = uiVisible,
                onBack = onBack,
                onDownloadPage = {
                    pagesState.pagesOrNull
                        ?.getOrNull(pagerState.currentPage)
                        ?.also { page -> onDownloadPage(page) }
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

        val contentModifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()

        when (pagesState) {
            is GalleryPagerComponent.PagesState.Error -> ErrorContent(contentModifier, pagesState)
            GalleryPagerComponent.PagesState.Loading -> LoadingContent(contentModifier)
            is GalleryPagerComponent.PagesState.Loaded ->
                GalleryPagerContent(
                    modifier = contentModifier,
                    pagerState = pagerState,
                    pagesState = pagesState,
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
                    pagesState.pagesOrNull
                        ?.getOrNull(pagerState.currentPage)
                        ?.also { page -> onSharePage(page, includeUrl) }
                    shareDialogVisible = false
                }
            )
        }
    }
}
