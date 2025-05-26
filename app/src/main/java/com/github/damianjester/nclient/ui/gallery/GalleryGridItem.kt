package com.github.damianjester.nclient.ui.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.damianjester.nclient.core.GalleryGridItemImage
import com.github.damianjester.nclient.core.GalleryLanguage
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import com.github.damianjester.nclient.ui.theme.NClientTheme
import io.ktor.http.Url

@Composable
fun GalleryGridItem(
    modifier: Modifier = Modifier,
    title: String,
    language: GalleryLanguage,
    image: GalleryGridItemImage,
    showHighRes: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.toFloat() / 4)
            .clickable(onClick = onClick),
        shape = Shape
    ) {
        Box {
            Image(
                modifier = Modifier.fillMaxSize(),
                image = image,
                showHighRes = showHighRes
            )

            LanguageIndicator(
                modifier = Modifier.align(Alignment.TopStart),
                language = language
            )

            Title(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                title = title
            )
        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    title: String,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
            .copy(alpha = SurfaceAlpha),
    ) {
        Text(
            title,
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun LanguageIndicator(
    modifier: Modifier = Modifier,
    language: GalleryLanguage,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
            .copy(alpha = SurfaceAlpha),
        shape = RoundedCornerShape(bottomEnd = 4.dp)
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
}

@Composable
private fun Image(
    modifier: Modifier = Modifier,
    image: GalleryGridItemImage,
    showHighRes: Boolean,
) {

    val model: Any = when (image) {
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
        model = model,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

private val Shape = RoundedCornerShape(8.dp)
private const val SurfaceAlpha = 0.75f

@PreviewLightDark
@Composable
private fun GalleryGridItemPreview() {
    NClientPreviewTheme {
        GalleryGridItem(
            title = "Title",
            language = GalleryLanguage.English,
            image = GalleryGridItemImage.Remote(
                thumbnailUrl = Url("https://t1.nhentai.net/galleries/{galleryId}/thumb.jpg"),
                coverUrl = Url("https://t1.nhentai.net/galleries/{galleryId}/cover.jpg")
            ),
            onClick = {}
        )
    }
}
