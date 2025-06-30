package com.github.damianjester.nclient.ui.gallery.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.damianjester.nclient.R.string.no_history
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.ui.gallery.common.AsciiEmojiMessage
import com.github.damianjester.nclient.ui.gallery.common.grid.GallerySummaryLazyGird
import kotlinx.coroutines.flow.Flow

@Composable
fun GalleryVisitLazyGrid(
    galleriesState: Flow<PagingData<GallerySummary>>,
    onGalleryClick: (GalleryId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val galleries = galleriesState.collectAsLazyPagingItems()

    if (galleries.itemCount == 0) {
        AsciiEmojiMessage(
            stringResource(no_history),
            modifier = modifier
        )
    } else {
        GallerySummaryLazyGird(
            modifier = modifier,
            galleries = galleries,
            onGalleryClick = onGalleryClick
        )
    }
}
