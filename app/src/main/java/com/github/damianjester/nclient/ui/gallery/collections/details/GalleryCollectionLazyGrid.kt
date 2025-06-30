package com.github.damianjester.nclient.ui.gallery.collections.details

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.ui.gallery.common.AsciiEmojiMessage
import com.github.damianjester.nclient.ui.gallery.common.grid.GalleryCard
import com.github.damianjester.nclient.ui.preview.PreviewData
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.Parcelize

@Composable
fun GalleryCollectionLazyGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    flow: Flow<PagingData<GallerySummary>>,
    onGalleryClick: (GalleryId) -> Unit,
    onAddToCollectionClick: (GalleryId) -> Unit,
    onRemoveFromCollectionClick: (GalleryId) -> Unit,
) {
    val lazyPagingItems = flow.collectAsLazyPagingItems()

    if (lazyPagingItems.itemCount == 0) {
        AsciiEmojiMessage(
            stringResource(R.string.gallery_collection_empty),
            modifier = modifier
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(180.dp),
            modifier = modifier,
            state = state,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                lazyPagingItems.itemCount,
                key = { key(it, lazyPagingItems) }
            ) { index ->

                val summary = lazyPagingItems[index]
                val itemModifier = Modifier
                    .fillMaxWidth()
                    .animateItem()

                if (summary != null) {
                    val onClick = remember(summary.id.value) { { onGalleryClick(summary.id) } }
                    val onAddToCollectionClick = remember(summary.id.value) { { onAddToCollectionClick(summary.id) } }
                    val onRemoveFromCollectionClick = remember(summary.id.value) { { onRemoveFromCollectionClick(summary.id) } }

                    GalleryCollectionItem(
                        onClick = onClick,
                        onAddToCollectionClick = onAddToCollectionClick,
                        onRemoveFromCollectionClick = onRemoveFromCollectionClick,
                        summary = summary,
                        modifier = itemModifier
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
}

private fun key(
    index: Int,
    items: LazyPagingItems<GallerySummary>,
): CollectionGridKey = (items.peek(index)
    ?.let { CollectionGridKey.Id(it.id.value) }
    ?: CollectionGridKey.IndexAt(index))

private sealed interface CollectionGridKey {
    @Parcelize
    data class Id(val id: Long) : CollectionGridKey, Parcelable

    @Parcelize
    data class IndexAt(val index: Int) : CollectionGridKey, Parcelable
}

@PreviewLightDark
@Composable
private fun GalleryCollectionLazyGridPreview() {
    NClientPreviewTheme {
        GalleryCollectionLazyGrid(
            flow = MutableStateFlow(PagingData.from(PreviewData.summaries)),
            onRemoveFromCollectionClick = {},
            onAddToCollectionClick = {},
            onGalleryClick = {}
        )
    }
}
