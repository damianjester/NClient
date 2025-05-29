package com.github.damianjester.nclient.ui.gallery.details.grid

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import coil3.compose.AsyncImage
import com.github.damianjester.nclient.core.GalleryImage
import com.github.damianjester.nclient.core.GalleryPage
import com.github.damianjester.nclient.core.GalleryPageImages
import com.github.damianjester.nclient.core.Resolution
import com.github.damianjester.nclient.ui.gallery.grid.GalleryGridItem
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import io.ktor.http.Url

@Composable
fun GalleryPageGridItem(
    modifier: Modifier = Modifier,
    page: GalleryPage,
    showHighRes: Boolean = false,
    onClick: () -> Unit,
) {
    GalleryGridItem(
        onClick = onClick,
        modifier = modifier
    ) {

        AsyncImage(
            model = page.toImageModel(showHighRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        GalleryPageIndicator(
            pageNumber = page.index + 1
        )
    }
}

@Composable
private fun GalleryPage.toImageModel(
    showHighRes: Boolean,
): Any = when (val image = image) {
    is GalleryPageImages.Local ->
        if (showHighRes) image.localOriginal else image.localThumbnail

    is GalleryPageImages.Remote ->
        (if (showHighRes) image.remoteOriginal.url else image.remoteThumbnail.url)
            .toString()
}

@PreviewLightDark
@Composable
private fun GalleryPageGridItemPreview() {
    NClientPreviewTheme {
        GalleryPageGridItem(
            page = GalleryPage(
                index = 5,
                image = GalleryPageImages.Remote(
                    remoteThumbnail = GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/[galleryId]/6t.webp.webp")),
                    remoteOriginal = GalleryImage.Remote(Url("https://i1.nhentai.net/galleries/[galleryId]/6.webp")),
                ),
                resolution = Resolution(width = 1280, height = 1870)
            ),
            showHighRes = false,
            onClick = {}
        )
    }
}
