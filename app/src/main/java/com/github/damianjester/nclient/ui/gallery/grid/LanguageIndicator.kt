package com.github.damianjester.nclient.ui.gallery.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.core.GalleryLanguage
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun BoxScope.LanguageIndicator(
    modifier: Modifier = Modifier,
    language: GalleryLanguage,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    GridItemIndicator(
        modifier = modifier,
        backgroundColor = backgroundColor
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

@PreviewLightDark
@Composable
private fun LanguageIndicatorPreview() {
    NClientPreviewTheme {
        Column {
            Box { LanguageIndicator(language = GalleryLanguage.English) }
            Box { LanguageIndicator(language = GalleryLanguage.Japanese) }
            Box { LanguageIndicator(language = GalleryLanguage.Chinese) }
        }
    }
}
