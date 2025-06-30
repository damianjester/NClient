package com.github.damianjester.nclient.ui.gallery.random

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.gallery.common.grid.GalleryCard
import com.github.damianjester.nclient.ui.gallery.common.grid.LanguageIndicator
import com.github.damianjester.nclient.ui.gallery.random.RandomGalleryComponent.RandomGalleryState

@Composable
fun LoadedContent(
    modifier: Modifier = Modifier,
    state: RandomGalleryState.Loaded,
    onGalleryClick: () -> Unit
) {
    val gallery = state.gallery

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.size(32.dp))

        Text(
            gallery.title.pretty,
            maxLines = 2,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.size(24.dp))

        GalleryCard(
            onClick = onGalleryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = gallery.cover.originalUrl.toString(),
                contentDescription = stringResource(R.string.gallery_cover),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            LanguageIndicator(
                modifier = Modifier.align(Alignment.TopStart),
                language = gallery.language,
            )
        }
    }
}
