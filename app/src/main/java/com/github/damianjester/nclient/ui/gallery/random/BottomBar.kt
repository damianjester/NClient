package com.github.damianjester.nclient.ui.gallery.random

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    model: RandomGalleryComponent.Model,
    onPreviousGalleryClick: () -> Unit,
    onRandomGalleryClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onPreviousGalleryClick,
            enabled = model.randoms.size > 1
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Undo,
                contentDescription = stringResource(R.string.previous_random_gallery_button),
            )
        }
        Spacer(Modifier.size(64.dp))
        IconButton(
            onRandomGalleryClick,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(),
        ) {
            Icon(
                Icons.Default.Shuffle,
                contentDescription = stringResource(R.string.next_random_gallery_button),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.size(64.dp))
        IconButton(onFavoriteClick) {
            Icon(
                if (model.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(R.string.favorite_gallery_button),
            )
        }
    }
}
