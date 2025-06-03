package com.github.damianjester.nclient.ui.gallery.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import coil3.compose.AsyncImage
import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.core.models.GalleryLanguage
import com.github.damianjester.nclient.core.models.GallerySearchItemImages
import com.github.damianjester.nclient.ui.gallery.grid.GalleryGridItem
import com.github.damianjester.nclient.ui.gallery.grid.LanguageIndicator
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import io.ktor.http.Url

@Composable
fun GallerySearchGridItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    language: GalleryLanguage,
    image: GallerySearchItemImages,
    showHighRes: Boolean = false,
) {
    GalleryGridItem(
        onClick = onClick,
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            image = image,
            showHighRes = showHighRes
        )

        LanguageIndicator(
            modifier = Modifier.align(Alignment.TopStart),
            language = language,
        )

        TitleStrip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            title = title,
        )
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

@PreviewLightDark
@Composable
private fun GallerySearchGridItemPreview() {
    NClientPreviewTheme {
        GallerySearchGridItem(
            onClick = {},
            title = "Title",
            language = GalleryLanguage.English,
            image = GallerySearchItemImages.Remote(
                thumbnail = GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/{galleryId}/thumb.jpg")),
            ),
        )
    }
}
