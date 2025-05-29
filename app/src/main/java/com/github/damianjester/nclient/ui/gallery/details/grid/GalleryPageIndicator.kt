package com.github.damianjester.nclient.ui.gallery.details.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.ui.gallery.grid.GridItemIndicator
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun BoxScope.GalleryPageIndicator(
    modifier: Modifier = Modifier,
    pageNumber: Int,
) {
    GridItemIndicator(
        modifier = modifier
    ) {
        Text(
            "$pageNumber",
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun GalleryPageIndicatorPreview() {
    NClientPreviewTheme {
        Box {
            GalleryPageIndicator(pageNumber = 21)
        }
    }
}
