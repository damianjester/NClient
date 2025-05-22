package com.github.damianjester.nclient.gallery

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.damianjester.nclient.GalleryGridItemImage
import com.github.damianjester.nclient.GalleryLanguage

@Composable
fun GalleryGridItem(
    modifier: Modifier = Modifier,
    title: String,
    language: GalleryLanguage,
    image: GalleryGridItemImage,
    showHighRes: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.toFloat() / 4)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .clickable { onClick() },
    ) {

        val imageModel: Any = when (val image = image) {
            is GalleryGridItemImage.Local -> {
                if (showHighRes) {
                    image.coverFile
                } else {
                    image.thumbnailFile
                }
            }
            is GalleryGridItemImage.Remote -> {
                if (showHighRes) {
                    image.coverUrl.toString()
                } else {
                    image.thumbnailUrl.toString()
                }
            }
        }

        AsyncImage(
            model = imageModel,
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

            val flag = when (language) {
                GalleryLanguage.Chinese -> "\uD83C\uDDE8\uD83C\uDDF3"
                GalleryLanguage.English -> "\uD83C\uDDEC\uD83C\uDDE7"
                GalleryLanguage.Japanese -> "\uD83C\uDDEF\uD83C\uDDF5"
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
