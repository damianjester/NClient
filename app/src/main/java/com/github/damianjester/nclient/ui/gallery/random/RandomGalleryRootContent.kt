package com.github.damianjester.nclient.ui.gallery.random

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.damianjester.nclient.ui.gallery.random.RandomGalleryComponent.RandomGalleryState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RandomGalleryRootContent(
    component: RandomGalleryComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()
    val galleryState = model.galleyState

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.random_gallery)
                    )
                },
                navigationIcon = {
                    DrawerMenuButton(onClick = onDrawerClick)
                }
            )
        }
    ) { innerPadding ->

        Column(
            Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.weight(1f)) {
                val contentModifier = Modifier.fillMaxSize()

                when (galleryState) {
                    is RandomGalleryState.Error -> ErrorContent(contentModifier, galleryState)
                    is RandomGalleryState.Loaded ->
                        LoadedContent(
                            modifier = contentModifier,
                            state = galleryState,
                            onGalleryClick = component::navigateToGallery
                        )
                    RandomGalleryState.Loading -> LoadingContent(contentModifier)
                }
            }

            BottomBar(
                modifier = Modifier.fillMaxWidth(),
                model = model,
                onPreviousGalleryClick = component::previousGallery,
                onRandomGalleryClick = component::fetchNextRandom,
                onFavoriteClick = component::favoriteGallery
            )
        }
    }
}
