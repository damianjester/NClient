package com.github.damianjester.nclient.ui.gallery.grid

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun TitleStrip(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
    ) {
        Text(
            title,
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@PreviewLightDark
@Composable
private fun TitleStripPreview() {
    NClientPreviewTheme {
        TitleStrip(
            "Hello, World!"
        )
    }
}
