package com.github.damianjester.nclient.ui.gallery.collections

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryCollectionId.Companion.FAVORITES_ID
import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.ui.preview.PreviewData
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import kotlinx.datetime.Instant

@Composable
@SuppressLint("UnusedBoxWithConstraintsScope")
fun CollectionImage(
    modifier: Modifier = Modifier,
    collection: GalleryCollection,
) {
    val thumbnails = collection.thumbnails

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = Shape,
    ) {
        BoxWithConstraints {
            val imageModifier = Modifier.fillMaxSize()

            if (maxWidth < 128.dp) {
                if (thumbnails.isEmpty()) {
                    EmptyCollectionImage(imageModifier, collection.isFavorites)
                } else {
                    CollectionAsyncImage(
                        thumbnails.first(),
                        modifier = imageModifier
                    )
                }
            } else {
                when (thumbnails.size) {
                    0 -> EmptyCollectionImage(imageModifier, collection.isFavorites)

                    1 -> CollectionAsyncImage(
                        thumbnails.first(),
                        modifier = imageModifier
                    )

                    2 -> DoubleImages(
                        modifier = imageModifier,
                        first = thumbnails.first(),
                        second = thumbnails[1]
                    )

                    else -> TripleImages(
                        modifier = imageModifier,
                        thumbnails = thumbnails
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCollectionImage(
    modifier: Modifier = Modifier,
    isFavorites: Boolean = false,
) {
    Card(modifier) {
        Box(Modifier.fillMaxSize()) {
            val vector = if (isFavorites) {
                Icons.Default.Favorite
            } else {
                Icons.Default.Collections
            }

            Icon(
                vector,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.5f),
                tint = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun CollectionAsyncImage(
    model: GalleryImage.Remote,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model.url.toString())
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun DoubleImages(
    modifier: Modifier = Modifier,
    first: GalleryImage.Remote,
    second: GalleryImage.Remote,
) {
    Row(modifier) {
        val weightModifier = Modifier.weight(1f)
        CollectionAsyncImage(
            first,
            modifier = weightModifier,
        )
        VerticalDivider(thickness = DividerThickness, color = DividerColor)
        CollectionAsyncImage(
            second,
            modifier = weightModifier,
        )
    }
}

@Composable
private fun TripleImages(
    modifier: Modifier = Modifier,
    thumbnails: List<GalleryImage.Remote>,
) {
    val (first, second, third) = thumbnails

    Row(modifier) {
        val weightModifier = Modifier.weight(1f)
        CollectionAsyncImage(
            first,
            modifier = weightModifier,
        )
        VerticalDivider(thickness = DividerThickness, color = DividerColor)
        Column(weightModifier) {
            CollectionAsyncImage(
                second,
                modifier = weightModifier,
            )
            HorizontalDivider(thickness = DividerThickness, color = DividerColor)
            CollectionAsyncImage(
                third,
                modifier = weightModifier,
            )
        }
    }
}

private val Shape = RoundedCornerShape(8.dp)
private val DividerColor = Color.Transparent
private val DividerThickness = 2.dp

private class CollectionImageParameterProvider : PreviewParameterProvider<GalleryCollection> {
    override val values: Sequence<GalleryCollection>
        get() = sequenceOf(
            GalleryCollection(GalleryCollectionId(FAVORITES_ID), "Favorites", Instant.fromEpochSeconds(1749546345), emptyList(), 0),
            *PreviewData.collections.toTypedArray()
        )
}

@Preview
@Composable
private fun CollectionImagePreview(
    @PreviewParameter(CollectionImageParameterProvider::class) collection: GalleryCollection
) {
    NClientPreviewTheme {
        CollectionImage(
            collection = collection
        )
    }
}
