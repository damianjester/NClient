package com.github.damianjester.nclient.ui.gallery.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GallerySearchRootContent(
    component: GallerySearchComponent,
    modifier: Modifier = Modifier,
) {
    // TODO: Gallery list
    // TODO: Pager controls
    // TODO: Drawer

    val state = component.model.subscribeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { TODO("Open navigation drawer") }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton({ TODO("Toggle TopAppbar search mode") }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                    IconButton({ TODO("Show language selector dialog") }) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = stringResource(R.string.languages)
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            GallerySearchGrid(
                galleries = state.value.galleries,
                onGalleryClick = component::navigateToGallery
            )
        }
    }
}
