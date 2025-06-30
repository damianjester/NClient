package com.github.damianjester.nclient.ui.gallery.collections.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.ui.gallery.common.grid.GallerySummaryGridItem
import com.github.damianjester.nclient.ui.preview.PreviewData
import com.github.damianjester.nclient.ui.theme.NClientTheme

@Composable
fun GalleryCollectionItem(
    onClick: () -> Unit,
    onAddToCollectionClick: () -> Unit,
    onRemoveFromCollectionClick: () -> Unit,
    summary: GallerySummary,
    modifier: Modifier = Modifier,
) {
    var isContextMenuVisible by remember {
        mutableStateOf(false)
    }

    GallerySummaryGridItem(
        onClick = onClick,
        onLongClick = {
            isContextMenuVisible = true
        },
        dropdownMenu = { offset ->
            DropdownMenu(
                expanded = isContextMenuVisible,
                onDismissRequest = { isContextMenuVisible = false },
                offset = offset,
            ) {
                fun onDropdownItemClick(block: () -> Unit): () -> Unit = {
                    isContextMenuVisible = false
                    block()
                }

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.add_to)) },
                    onClick = onDropdownItemClick { onAddToCollectionClick() },
                    leadingIcon = {
                        Icon(Icons.Default.Add, null)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.remove)) },
                    onClick = onDropdownItemClick { onRemoveFromCollectionClick() },
                    leadingIcon = {
                        Icon(Icons.Default.Remove, null)
                    }
                )
            }
        },
        summary = summary,
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun GalleryCollectionItemPreview() {
    NClientTheme {
        GalleryCollectionItem(
            onClick = {},
            onAddToCollectionClick = {},
            onRemoveFromCollectionClick = {},
            summary = PreviewData.summaries.first(),
        )
    }
}
