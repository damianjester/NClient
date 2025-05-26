package com.github.damianjester.nclient.ui.gallery.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.ui.theme.NClientTheme

@Composable
fun GalleryTag(
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    GalleryTag(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
    ) {
        Text(tag)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun GalleryTag(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onLongClick = onLongClick,
                onClick = onClick
            ),
        shape = Shape,
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
            Row(
                modifier = Modifier
                    .heightIn(min = MinHeight)
                    .padding(horizontal = HorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

private val Shape = RoundedCornerShape(50)
private val MinHeight = 32.dp
private val HorizontalPadding = 12.dp

@Preview
@Composable
private fun GalleryTagPreview() {
    NClientTheme {
        GalleryTag(
            "full color",
            onClick = {}
        )
    }
}
