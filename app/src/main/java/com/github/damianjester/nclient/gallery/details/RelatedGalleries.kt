package com.github.damianjester.nclient.gallery.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun RelatedGalleriesSection(
    modifier: Modifier = Modifier,
    galleries: List<GalleryDetailsComponent.RelatedGallery>,
    onGalleryClick: (GalleryDetailsComponent.RelatedGallery) -> Unit
) {
    Column(modifier) {

        Text(
            "Related",
            style = MaterialTheme.typography.h4
        )

        Spacer(Modifier.height(8.dp))

        RelatedGalleryScroller(
            modifier = Modifier.fillMaxWidth(),
            galleries = galleries,
            onGalleryClick = onGalleryClick
        )
    }
}

@Composable
fun RelatedGalleryScroller(
    modifier: Modifier = Modifier,
    galleries: List<GalleryDetailsComponent.RelatedGallery>,
    onGalleryClick: (GalleryDetailsComponent.RelatedGallery) -> Unit
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        galleries.forEach { gal ->
            RelatedGalleryCover(
                title = gal.title,
                coverImageUrl = gal.coverImageUrl,
                locale = gal.locale,
                onGalleryClick = { onGalleryClick(gal) }
            )
        }
    }
}

@Composable
fun RelatedGalleryCover(
    modifier: Modifier = Modifier,
    title: String,
    coverImageUrl: String,
    locale: GalleryDetailsComponent.GalleryLocale, // TODO: Use to show locale in top left corner
    onGalleryClick: () -> Unit
) {
    Box(
        modifier
            .width(256.dp)
            .aspectRatio(3.toFloat() / 4)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .clickable { onGalleryClick() }
    ) {

        AsyncImage(
            model = coverImageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart),
            color = MaterialTheme.colors.surface
                .copy(alpha = 0.5f),
            shape = RoundedCornerShape(
                bottomEnd = 4.dp,
            )
        ) {

            val flag = when (locale) {
                GalleryDetailsComponent.GalleryLocale.Chinese -> "\uD83C\uDDE8\uD83C\uDDF3"
                GalleryDetailsComponent.GalleryLocale.English -> "\uD83C\uDDEC\uD83C\uDDE7"
                GalleryDetailsComponent.GalleryLocale.Japanese -> "\uD83C\uDDEF\uD83C\uDDF5"
                else -> "\uD83C\uDFF3"
            }

            Text(
                flag,
                modifier = Modifier.padding(4.dp),
                textAlign = TextAlign.Center
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colors.surface
                .copy(alpha = 0.8f),
        ) {
            Text(
                title,
                modifier = Modifier.padding(4.dp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

