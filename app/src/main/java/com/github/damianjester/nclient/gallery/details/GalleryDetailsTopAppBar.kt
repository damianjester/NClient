package com.github.damianjester.nclient.gallery.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
fun GalleryDetailsTopAppBar(
    modifier: Modifier = Modifier,
    galleryTitle: String?,
    isGalleryFavorite: Boolean,
    gridMode: GalleryDetailsComponent.GridMode,
    onBackClick: () -> Unit,
    onTitleLongClick: (String) -> Unit,
    onFavoriteClick: (Boolean) -> Unit,
    onGridModeClick: () -> Unit,
    onCommentsClick: () -> Unit
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
        windowInsets = WindowInsets.statusBarsIgnoringVisibility,
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
                DropdownMenuItem(onCommentsClick) {
                    Icon(Icons.AutoMirrored.Default.Comment, contentDescription = null)
                    Text("Show comments")
                }
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )
}
