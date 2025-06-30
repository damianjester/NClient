package com.github.damianjester.nclient.ui.gallery.common.grid

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.GallerySummaryImages
import com.github.damianjester.nclient.ui.gallery.search.TitleStrip
import com.github.damianjester.nclient.ui.preview.PreviewData
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun GallerySummaryGridItem(
    onClick: () -> Unit,
    summary: GallerySummary,
    modifier: Modifier = Modifier,
    showHighRes: Boolean = false,
) {
    GalleryCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        CardContents(
            summary = summary,
            showHighRes = showHighRes
        )
    }
}

@Composable
fun GallerySummaryGridItem(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    dropdownMenu: @Composable (offset: DpOffset) -> Unit,
    summary: GallerySummary,
    modifier: Modifier = Modifier,
    showHighRes: Boolean = false,
) {
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }

    var itemHeight by remember {
        mutableStateOf(0.dp)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val density = LocalDensity.current

    GalleryCard(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        Box(
            Modifier
                .indication(interactionSource, LocalIndication.current)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                            onLongClick()
                        },
                        onTap = {
                            onClick()
                        },
                        onPress = {
                            val press = PressInteraction.Press(it)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                        }
                    )
                },
        ) {
            CardContents(
                summary = summary,
                showHighRes = showHighRes
            )

            dropdownMenu(
                pressOffset.copy(
                    y = pressOffset.y - itemHeight
                )
            )
        }
    }
}

@Composable
private fun BoxScope.CardContents(
    summary: GallerySummary,
    showHighRes: Boolean,
) {
    Image(
        modifier = Modifier.fillMaxSize(),
        image = summary.images,
        showHighRes = showHighRes
    )

    LanguageIndicator(
        modifier = Modifier.align(Alignment.TopStart),
        language = summary.language,
    )

    TitleStrip(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
        title = summary.title,
    )
}

@Composable
private fun Image(
    modifier: Modifier = Modifier,
    image: GallerySummaryImages,
    showHighRes: Boolean,
) {
    val model: Any = when (image) {
        is GallerySummaryImages.Local -> {
            if (showHighRes) {
                image.cover
            } else {
                image.thumbnail
            }
        }

        is GallerySummaryImages.Remote -> image.thumbnail.url.toString()
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
        GallerySummaryGridItem(
            onClick = {},
            summary = PreviewData.summaries.first(),
            modifier = Modifier.width(200.dp)
        )
    }
}
