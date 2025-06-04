package com.github.damianjester.nclient.ui.gallery.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.damianjester.nclient.ui.gallery.search.GallerySearchComponent.GalleriesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GallerySearchRootContent(
    component: GallerySearchComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by component.model.subscribeAsState()
    val galleriesState = state.galleriesState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDrawerClick) {
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

        val contentModifier = modifier
            .padding(padding)
            .fillMaxSize()

        when (galleriesState) {
            GalleriesState.Loading -> LoadingContent(contentModifier)
            is GalleriesState.Error -> ErrorContent(contentModifier, galleriesState)
            is GalleriesState.Loaded ->
                // TODO: Handle when no galleries have been found
                GallerySearchContent(
                    modifier = contentModifier,
                    galleries = galleriesState.galleries,
                    onGalleryClick = component::navigateToGallery
                )
        }
    }
}
