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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        items(galleries, key = { it.id.value }) { gal ->

            val onClick = remember(gal.id.value) { { onGalleryClick(gal.id) } }

            GallerySummaryGridItem(
                modifier = Modifier.fillMaxWidth(),
                title = gal.title,
                language = gal.language,
                image = gal.images,
                onClick = onClick
            )
        }
    }
}
