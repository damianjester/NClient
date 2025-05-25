package com.github.damianjester.nclient.ui.gallery.pager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun TopAppBar(
    modifier: Modifier = Modifier,
    galleryTitle: String?,
    visible: Boolean = true,
    onBack: () -> Unit,
    onDownloadPage: () -> Unit,
    onSharePage: () -> Unit,
) {
    AnimatedVisibility(
        visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        TopAppBar(
            title = {
                if (galleryTitle != null) {
                    Text(
                        galleryTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            windowInsets = WindowInsets.statusBarsIgnoringVisibility,
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        stringResource(R.string.back)
                    )
                }
            },
            actions = {
                IconButton(onClick = onDownloadPage) {
                    Icon(
                        Icons.Default.Save,
                        stringResource(R.string.download_page)
                    )
                }
                IconButton(onClick = onSharePage) {
                    Icon(
                        Icons.Default.Share,
                        stringResource(R.string.share_page)
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.surface
                .copy(alpha = 0.9f),
            elevation = 0.dp
        )
    }
}
