@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.damianjester.nclient.ui.gallery.collections.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.common.LoadingContent
import com.github.damianjester.nclient.ui.drawer.DrawerMenuButton
import com.github.damianjester.nclient.ui.gallery.collections.list.GalleryCollectionsComponent.CollectionsState
import com.github.damianjester.nclient.ui.preview.PreviewScreen
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun GalleryCollectionsRootContent(
    component: GalleryCollectionsComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()
    val collectionState = model.collectionsState

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.gallery_collections_topbar_title)
                    )
                },
                navigationIcon = {
                    DrawerMenuButton(onDrawerClick)
                },
                actions = {
                    IconButton(component::activateSortDialog) {
                        Icon(
                            Icons.AutoMirrored.Default.Sort,
                            contentDescription = stringResource(R.string.sort_collections)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(component::activateCreateCollectionDialog) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.gallery_collection_cd_add_collection)
                )
            }
        }
    ) { innerPadding ->

        val contentModifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()

        when (collectionState) {
            CollectionsState.Loading -> LoadingContent()
            is CollectionsState.Loaded ->
                LoadedContent(
                    modifier = contentModifier,
                    state = collectionState,
                    onCollectionClick = component::navigateToCollection
                )
        }
    }
}

@PreviewScreen
@Composable
private fun GalleryCollectionsRootContentPreview() {
    NClientPreviewTheme {
        GalleryCollectionsRootContent(
            component = PreviewGalleryCollectionsComponent(),
            onDrawerClick = {}
        )
    }
}
