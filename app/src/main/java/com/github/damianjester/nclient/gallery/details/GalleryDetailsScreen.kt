package com.github.damianjester.nclient.gallery.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun GalleryDetailsScreen(
    modifier: Modifier = Modifier,
    component: GalleryDetailsComponent,
    onBack: () -> Unit,
    onTagClick: (GalleryDetailsComponent.GalleryTag) -> Unit,
    onPageClick: (GalleryDetailsComponent.GalleryPage) -> Unit,
    onRelatedGalleryClick: (GalleryDetailsComponent.RelatedGallery) -> Unit,
    onCopyMetadata: (GalleryDetailsComponent.MetadataCopy) -> Unit
) {
    val model by component.model.subscribeAsState()
    val galleryState = model.galleryState
    Box(modifier.background(Color.Red))

    Scaffold(
        modifier = modifier,
        topBar = {
            val galleryTitle = when (galleryState) {
                is GalleryDetailsComponent.GalleryState.Loaded -> galleryState.gallery.title
                else -> null
            }
            val isGalleryFavorite = when (galleryState) {
                is GalleryDetailsComponent.GalleryState.Loaded -> galleryState.gallery.isFavorite
                else -> false
            }

            GalleryDetailsTopAppBar(
                galleryTitle = galleryTitle,
                isGalleryFavorite = isGalleryFavorite,
                onBackClick = onBack,
                onTitleLongClick = {
                    onCopyMetadata(GalleryDetailsComponent.MetadataCopy.Title(it))
                },
                onFavoriteClick = component::setGalleryFavoriteStatus,
                gridMode = model.gridMode,
                onGridModeClick = component::toggleGridMode
            )
        }
    ) { innerPadding ->
        if (galleryState is GalleryDetailsComponent.GalleryState.Loaded) {
            GalleryPagesLazyGrid(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                gallery = galleryState.gallery,
                gridMode = model.gridMode,
                onPageClick = onPageClick,
                onRelatedGalleryClick = onRelatedGalleryClick,
                onTagClick = onTagClick,
                onCopyMetadata = onCopyMetadata
            )
        }
    }
}
