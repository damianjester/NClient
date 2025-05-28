package com.github.damianjester.nclient.ui.gallery.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.damianjester.nclient.core.GalleryImage
import com.github.damianjester.nclient.core.GalleryLanguage
import com.github.damianjester.nclient.core.GallerySearchItemImages
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import io.ktor.http.Url

@Composable
fun GalleryGridItem(
    modifier: Modifier = Modifier,
    title: String,
    language: GalleryLanguage,
    image: GallerySearchItemImages,
    showHighRes: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.toFloat() / 4),
        shape = Shape
    ) {
        Box {
            Image(
                modifier = Modifier.fillMaxSize(),
                image = image,
                showHighRes = showHighRes
            )

            val metadataBackground = MaterialTheme.colorScheme.surface
                .copy(alpha = SurfaceAlpha)

            LanguageIndicator(
                modifier = Modifier.align(Alignment.TopStart),
                language = language,
                backgroundColor = metadataBackground
            )

            TitleStrip(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                title = title,
                backgroundColor = metadataBackground
            )
        }
    }
}

@Composable
private fun Image(
    modifier: Modifier = Modifier,
    image: GallerySearchItemImages,
    showHighRes: Boolean,
) {
    val model: Any = when (image) {
        is GallerySearchItemImages.Local -> {
            if (showHighRes) {
                image.cover
            } else {
                image.thumbnail
            }
        }
        is GallerySearchItemImages.Remote -> image.thumbnail.url.toString()
    }

    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

private val Shape = RoundedCornerShape(8.dp)
private const val SurfaceAlpha = 0.8f

@PreviewLightDark
@Composable
private fun GalleryGridItemPreview() {
    NClientPreviewTheme {
        GalleryGridItem(
            title = "Title",
            language = GalleryLanguage.English,
            image = GallerySearchItemImages.Remote(
                thumbnail = GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/{galleryId}/thumb.jpg")),
            ),
            onClick = {}
        )
    }
}
