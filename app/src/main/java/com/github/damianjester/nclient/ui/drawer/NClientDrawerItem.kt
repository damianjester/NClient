package com.github.damianjester.nclient.ui.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.About
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Bookmarks
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Collections
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Downloads
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Galleries
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.History
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.RandomGallery
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem.Settings
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun NClientDrawerItemSection(
    modifier: Modifier = Modifier,
    items: List<Pair<NClientDrawerItem, Boolean>>,
    onClick: (NClientDrawerItem) -> Unit,
    selectedItem: NClientDrawerItem?,
) {
    Column(modifier = modifier.padding(8.dp)) {
        items.forEach { (item, todo) ->
            NClientDrawerItem(
                item = item,
                onClick = onClick,
                selected = item == selectedItem,
                todo = todo
            )
        }
    }
}

@Composable
fun NClientDrawerItem(
    modifier: Modifier = Modifier,
    item: NClientDrawerItem,
    onClick: (NClientDrawerItem) -> Unit,
    selected: Boolean,
    todo: Boolean,
) {
    val label: String = when (item) {
        Galleries -> R.string.galleries
        Downloads -> R.string.downloads
        RandomGallery -> R.string.random_gallery
        Collections -> R.string.collections
        Bookmarks -> R.string.bookmarks
        History -> R.string.history
        Settings -> R.string.settings
        About -> R.string.about
    }.let { stringResource(it) }

    val icon = when (item) {
        Galleries -> Icons.Default.GridView
        Downloads -> Icons.Default.Download
        RandomGallery -> Icons.Default.Shuffle
        Collections -> Icons.Default.Collections
        Bookmarks -> Icons.Default.Bookmark
        History -> Icons.Default.History
        Settings -> Icons.Default.Settings
        About -> Icons.Default.Info
    }

    NavigationDrawerItem(
        label = { Text(text = label) },
        selected = selected,
        onClick = { onClick(item) },
        modifier = modifier,
        icon = { Icon(icon, null) },
        badge = if (todo) {
            ({
                Text(
                    "TODO",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            })
        } else {
            null
        },
        shape = RoundedCornerShape(8.dp)
    )
}

@PreviewLightDark
@Composable
private fun NClientDrawerItemPreview(
    @PreviewParameter(NClientDrawerItemPreviewProvider::class) item: NClientDrawerItem,
) {
    NClientPreviewTheme {
        Surface {
            Box(Modifier.padding(16.dp)) {
                NClientDrawerItem(
                    modifier = Modifier.width(320.dp),
                    item = item,
                    onClick = {},
                    selected = item == Galleries,
                    todo = item != Galleries
                )
            }
        }
    }
}

private class NClientDrawerItemPreviewProvider : PreviewParameterProvider<NClientDrawerItem> {
    override val values: Sequence<NClientDrawerItem> = NClientDrawerItem.entries.asSequence()
}
