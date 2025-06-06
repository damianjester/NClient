package com.github.damianjester.nclient.ui.gallery.common.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun BoxScope.GridItemIndicator(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = backgroundColor.copy(alpha = 0.8f),
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(bottomEnd = 4.dp),
        content = content
    )
}

@Preview
@Composable
private fun GridItemIndicatorPreview() {
    NClientPreviewTheme {
        Box {
            GridItemIndicator {
                Text("1")
            }
        }
    }
}
