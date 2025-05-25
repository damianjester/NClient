package com.github.damianjester.nclient.ui.gallery.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

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
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.surface, Shape)
            .clip(Shape)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onLongClick = onLongClick,
                onClick = onClick
            ),
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.body2) {
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
