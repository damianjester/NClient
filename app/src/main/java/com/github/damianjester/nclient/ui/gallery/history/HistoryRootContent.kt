package com.github.damianjester.nclient.ui.gallery.history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.drawer.DrawerMenuButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryRootContent(
    component: HistoryComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var openClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history)) },
                navigationIcon = {
                    DrawerMenuButton(onClick = onDrawerClick)
                },
                actions = {
                    IconButton(component::activateSortDialog) {
                        Icon(
                            Icons.AutoMirrored.Default.Sort,
                            contentDescription = stringResource(R.string.sort_history)
                        )
                    }

                    IconButton({ openClearDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.clear_history)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        GalleryVisitLazyGrid(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            galleriesState = component.galleries,
            onGalleryClick = component::navigateToGallery,
        )

        if (openClearDialog) {
            ClearHistoryDialog(
                onDismissRequest = { openClearDialog = false },
                onConfirmation = {
                    openClearDialog = false
                    component.clearHistory()
                }
            )
        }
    }
}
