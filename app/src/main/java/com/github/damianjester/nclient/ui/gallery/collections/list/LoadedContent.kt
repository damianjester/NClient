package com.github.damianjester.nclient.ui.gallery.collections.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.ui.gallery.collections.CollectionImage
import com.github.damianjester.nclient.ui.gallery.collections.list.GalleryCollectionsComponent.CollectionsState

@Composable
fun LoadedContent(
    modifier: Modifier = Modifier,
    state: CollectionsState.Loaded,
    onCollectionClick: (GalleryCollectionId) -> Unit,
) {
    LazyVerticalGrid(
        GridCells.Adaptive(128.dp),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(state.collections, key = { it.id.value }) { collection ->

            val onClick: () -> Unit = remember(collection) { { onCollectionClick(collection.id) } }

            CollectionGridItem(
                collection = collection,
                onClick = onClick
            )
        }
    }
}

@Composable
fun CollectionGridItem(
    modifier: Modifier = Modifier,
    collection: GalleryCollection,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CollectionImage(
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp),
            collection = collection,
        )

        Text(
            buildAnnotatedString {
                append(if (collection.isFavorites) stringResource(R.string.favorites) else collection.name)

                if (collection.size > 0) {
                    val style = MaterialTheme.typography.bodyMedium.toSpanStyle()
                        .copy(color = LocalContentColor.current.copy(alpha = 0.66f))

                    append(" ")

                    withStyle(style) {
                        append("(${collection.size})")
                    }
                }
            },
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
    }
}
