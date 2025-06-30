package com.github.damianjester.nclient.ui.gallery.common.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary

@Composable
fun GallerySummaryLazyGird(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    galleries: List<GallerySummary>,
    onGalleryClick: (GalleryId) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(galleries, key = { it.id.value }) { summary ->

            val onClick = remember(summary.id.value) { { onGalleryClick(summary.id) } }

            GallerySummaryGridItem(
                onClick = onClick,
                summary = summary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun GallerySummaryLazyGird(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    galleries: LazyPagingItems<GallerySummary>,
    onGalleryClick: (GalleryId) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(galleries.itemCount, key = { lazyPagingKey(it, galleries) }) { index ->

            val itemModifier = Modifier.fillMaxWidth()
            val summary = galleries[index]

            if (summary != null) {
                val onClick = remember(summary.id.value) { { onGalleryClick(summary.id) } }

                GallerySummaryGridItem(
                    onClick = onClick,
                    summary = summary,
                    modifier = itemModifier,
                )
            } else {
                GalleryCard(
                    modifier = itemModifier,
                    content = {}
                )
            }
        }
    }
}
