package com.github.damianjester.nclient.ui.gallery.history

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.ui.common.reachedBottom
import com.github.damianjester.nclient.ui.gallery.common.AsciiEmojiMessage
import com.github.damianjester.nclient.ui.gallery.common.grid.GallerySummaryLazyGird
import com.github.damianjester.nclient.ui.gallery.history.HistoryComponent.GalleriesState

@Composable
fun LoadedContent(
    modifier: Modifier = Modifier,
    galleriesState: GalleriesState.Loaded,
    onGalleryClick: (GalleryId) -> Unit,
    onBottomReached: () -> Unit
) {
    val galleries = galleriesState.galleries
    val gridState = rememberLazyGridState()

    val reachedBottom: Boolean by remember {
        derivedStateOf { gridState.reachedBottom() }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom) onBottomReached()
    }

    if (galleries.isEmpty()) {
        NoHistory(modifier)
    } else {
        GallerySummaryLazyGird(
            modifier = modifier,
            state = gridState,
            galleries = galleries,
            onGalleryClick = onGalleryClick
        )
    }
}

@Composable
fun NoHistory(modifier: Modifier = Modifier) {
    AsciiEmojiMessage(
        stringResource(R.string.no_history),
        modifier = modifier
    )
}
