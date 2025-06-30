package com.github.damianjester.nclient.ui.gallery.common.grid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun GalleryCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.toFloat() / 4),
        shape = Shape
    ) {
        Box(content = content)
    }
}

@Composable
fun GalleryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    GalleryCard(
        modifier = modifier.clickable(onClick = onClick),
        content = content
    )
}

private val Shape = RoundedCornerShape(8.dp)

@Preview
@Composable
private fun GalleryCardPreview() {
    NClientPreviewTheme {
        GalleryCard {
            Text(
                "Hello, World!",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}
