package com.github.damianjester.nclient.ui.gallery.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.ui.common.reachedBottom
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge,
        ) {
            Text(
                stringResource(R.string.no_history),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(2.dp))
            Text(
                stringResource(R.string.ascii_koala),
                modifier = Modifier.clearAndSetSemantics {},
            )
        }
    }
}
