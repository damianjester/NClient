package com.github.damianjester.nclient.ui.gallery.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.ui.gallery.common.grid.GallerySummaryLazyGird

@Composable
fun GallerySearchContent(
    modifier: Modifier = Modifier,
    galleries: List<GallerySummary>,
    onGalleryClick: (GalleryId) -> Unit,
) {
    GallerySummaryLazyGird(
        modifier = modifier,
        galleries = galleries,
        onGalleryClick = onGalleryClick
    )
}
