package com.github.damianjester.nclient.ui.gallery.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
fun GalleryDetailsTopAppBar(
    modifier: Modifier = Modifier,
    galleryTitle: String?,
    isGalleryFavorite: Boolean,
    gridMode: GalleryDetailsComponent.GridMode,
    onBackClick: () -> Unit,
    onTitleLongClick: (String) -> Unit,
    onFavoriteClick: (Boolean) -> Unit,
    onGridModeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onShareClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (galleryTitle != null) {
                Text(
                    galleryTitle,
                    modifier = Modifier
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onLongClick = { onTitleLongClick(galleryTitle) },
                            onClick = {}
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(onClick = { onFavoriteClick(!isGalleryFavorite) }) {
                Icon(
                    if (isGalleryFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    stringResource(R.string.toggle_favorite)
                )
            }
            IconButton(onClick = onGridModeClick) {
                val icon = when (gridMode) {
                    GalleryDetailsComponent.GridMode.ONE_COLUMN -> R.drawable.ic_view_1
                    GalleryDetailsComponent.GridMode.TWO_COLUMNS -> R.drawable.ic_view_2
                    GalleryDetailsComponent.GridMode.THREE_COLUMNS -> R.drawable.ic_view_3
                    GalleryDetailsComponent.GridMode.FOUR_COLUMNS -> R.drawable.ic_view_4
                }

                Icon(
                    painterResource(icon),
                    stringResource(R.string.change_view)
                )
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null // TODO
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Show comments") },
                    onClick = { onCommentsClick() },
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Default.Comment,
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.share)) },
                    onClick = onShareClick,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null
                        )
                    }
                )
            }
        },
    )
}

@PreviewLightDark
@Composable
private fun GalleryDetailsTopAppBarPreview() {
    NClientPreviewTheme {
        GalleryDetailsTopAppBar(
            galleryTitle = "Hello, World!",
            isGalleryFavorite = true,
            gridMode = GalleryDetailsComponent.GridMode.TWO_COLUMNS,
            onBackClick = {},
            onTitleLongClick = {},
            onFavoriteClick = {},
            onGridModeClick = {},
            onCommentsClick = {},
            onShareClick = {}
        )
    }
}
