package com.github.damianjester.nclient.ui.gallery.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.core.GallerySearchItem

@Composable
fun GallerySearchGrid(
    modifier: Modifier = Modifier,
    galleries: List<GallerySearchItem>,
    onGalleryClick: (GallerySearchItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(galleries, key = { it.id.value }) { gal ->

            val onClick = remember(gal.id.value) { { onGalleryClick(gal) } }

            GallerySearchGridItem(
                modifier = Modifier.fillMaxWidth(),
                title = gal.title,
                language = gal.language,
                image = gal.images,
                onClick = onClick
            )
        }
    }
}
