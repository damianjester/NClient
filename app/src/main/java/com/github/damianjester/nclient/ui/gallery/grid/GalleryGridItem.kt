package com.github.damianjester.nclient.ui.gallery.grid

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
fun GalleryGridItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.toFloat() / 4),
        shape = Shape
    ) {
        Box(content = content)
    }
}

private val Shape = RoundedCornerShape(8.dp)

@Preview
@Composable
private fun GalleryGridItemPreview() {
    NClientPreviewTheme {
        GalleryGridItem({}) {
            Text(
                "Grid item",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
