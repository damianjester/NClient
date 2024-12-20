package com.github.damianjester.nclient.gallery.pager

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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.request.ImageRequest
import com.github.damianjester.nclient.gallery.pager.GalleryPagerComponent.GalleryPageImage
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun GalleryPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    pages: List<GalleryPagerComponent.GalleryPage>,
    onPageClick: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        PageIndicator(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp)
                .windowInsetsPadding(WindowInsets.statusBarsIgnoringVisibility),
            pagerState = pagerState
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize(),
        ) { i ->

            val model: Any = when (val model = pages[i].image) {
                is GalleryPageImage.Local -> model.file
                is GalleryPageImage.Remote -> model.url
            }

            ZoomableAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                state = rememberZoomableImageState(
                    rememberZoomableState(
                        ZoomSpec(
                            maxZoomFactor = 2.5f
                        )
                    )
                ),
                contentScale = ContentScale.Fit,
                onClick = { onPageClick() }
            )
        }

    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
    ) {
        Text(
            "${pagerState.currentPage + 1}/${pagerState.pageCount}",
            modifier = Modifier
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}
