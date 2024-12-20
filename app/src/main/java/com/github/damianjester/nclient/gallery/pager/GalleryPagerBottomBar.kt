package com.github.damianjester.nclient.gallery.pager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R
import kotlinx.coroutines.launch

@Composable
fun GalleryPagerBottomBar(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    pagerState: PagerState,
    onJumpToPage: () -> Unit,
) {

    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Surface(
            color = MaterialTheme.colors.surface
                .copy(alpha = 0.9f)
        ) {
            Column(
                Modifier
                    .padding(
                        horizontal = 32.dp,
                        vertical = 16.dp
                    )
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                var sliding by remember { mutableStateOf(false) }
                var targetPageIndex by remember { mutableIntStateOf(0) }
                var sliderValue by remember {
                    mutableFloatStateOf((pagerState.currentPage + 1).toFloat() / pagerState.pageCount)
                }

                LaunchedEffect(pagerState.currentPage) {
                    sliderValue = (pagerState.currentPage + 1).toFloat() / pagerState.pageCount
                }

                TopRow(
                    modifier = Modifier.fillMaxWidth(),
                    pagerState = pagerState,
                    sliding = sliding,
                    targetPageIndex = targetPageIndex,
                    onJumpToPage = onJumpToPage,
                )

                Spacer(Modifier.height(8.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliding = true
                        sliderValue = it
                        targetPageIndex = (pagerState.pageCount * sliderValue).toInt()
                    },
                    onValueChangeFinished = {
                        sliding = false
                        scope.launch {
                            pagerState.scrollToPage(targetPageIndex)
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TopRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    sliding: Boolean,
    targetPageIndex: Int,
    onJumpToPage: () -> Unit,
) {

    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            enabled = pagerState.canScrollBackward,
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.cd_page_backwards)
            )
        }
        TextButton(
            onClick = onJumpToPage
        ) {
            val text = if (sliding) {
                "${targetPageIndex + 1}/${pagerState.pageCount}"
            } else {
                "${pagerState.currentPage + 1}/${pagerState.pageCount}"
            }
            Text(text)
        }
        IconButton(
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            enabled = pagerState.canScrollForward,
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = stringResource(R.string.cd_page_forwards)
            )
        }
    }
}
