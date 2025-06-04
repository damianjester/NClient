package com.github.damianjester.nclient.ui.gallery.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.request.ImageRequest
import com.github.damianjester.nclient.core.models.GalleryPageImages
import com.github.damianjester.nclient.ui.gallery.pager.GalleryPagerComponent.PagesState
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun GalleryPagerContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    pagesState: PagesState.Loaded,
    onPageClick: () -> Unit,
) {
    val pages = pagesState.pages

    Box(
        modifier = modifier
    ) {

        var userScrollEnabled by remember { mutableStateOf(true) }
        val currentPage by remember { derivedStateOf { pagerState.currentPage } }

        LaunchedEffect(currentPage) {
            // When the pager's current page changes, enabled user scrolling, regardless of
            // whether the image is zoomed in or not
            userScrollEnabled = true
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = userScrollEnabled
        ) { i ->

            val model: Any = when (val model = pages[i].image) {
                is GalleryPageImages.Local -> model.localOriginal
                is GalleryPageImages.Remote -> model.remoteOriginal.url.toString()
            }

            val zoomState = rememberZoomableState(
                ZoomSpec(
                    maxZoomFactor = 2.5f
                )
            )

            val zoomedOut by remember {
                derivedStateOf {
                    zoomState.zoomFraction?.let { it < 0.01f } != false
                }
            }

            LaunchedEffect(zoomedOut, currentPage) {
                if (currentPage == i) {
                    // Toggle user scrolling on the pager based on:
                    // - image zoomed level (zoomed in = off, zoomed out = on)
                    // - the page of the pager, checking that this page is the current page,
                    //   i.e. the user has fully scrolled to it
                    //
                    // The last check is particularly important because otherwise other off-screen pages
                    // will prevent the user from scrolling the pager
                    userScrollEnabled = zoomedOut
                }
            }

            ZoomableAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                state = rememberZoomableImageState(zoomState),
                contentScale = ContentScale.Fit,
                onClick = { onPageClick() },
            )
        }

        PageIndicator(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp)
                .windowInsetsPadding(WindowInsets.statusBarsIgnoringVisibility),
            pagerState = pagerState
        )
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
        tonalElevation = 4.dp
    ) {
        Text(
            "${pagerState.currentPage + 1}/${pagerState.pageCount}",
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}
