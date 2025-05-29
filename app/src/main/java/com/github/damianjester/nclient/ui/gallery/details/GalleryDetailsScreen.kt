package com.github.damianjester.nclient.ui.gallery.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryPage
import com.github.damianjester.nclient.core.GalleryTag

@Composable
fun GalleryDetailsRootContent(
    component: GalleryDetailsComponent,
    modifier: Modifier = Modifier
) {
    GalleryDetailsScreen(
        modifier = modifier,
        component = component,
        onBack = component::navigateBack,
        onTagClick = { TODO() },
        onPageClick = { page -> component.navigateToPage(page.index) },
        onRelatedGalleryClick = component::navigateRelated,
        onCopyMetadata = { component.copyToClipboard(it) }
    )
}

@Composable
fun GalleryDetailsScreen(
    modifier: Modifier = Modifier,
    component: GalleryDetailsComponent,
    onBack: () -> Unit,
    onTagClick: (GalleryTag) -> Unit,
    onPageClick: (GalleryPage) -> Unit,
    onRelatedGalleryClick: (GalleryId) -> Unit,
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
                is GalleryDetailsComponent.GalleryState.Loaded -> {
//                    galleryState.gallery.isFavorite // TODO: Add favorite property
                    false
                }
                else -> {
                    false
                }
            }

            GalleryDetailsTopAppBar(
                galleryTitle = galleryTitle?.pretty,
                isGalleryFavorite = isGalleryFavorite,
                onBackClick = onBack,
                onTitleLongClick = {
                    onCopyMetadata(GalleryDetailsComponent.MetadataCopy.Title(it))
                },
                onFavoriteClick = component::setGalleryFavoriteStatus,
                gridMode = model.gridMode,
                onGridModeClick = component::toggleGridMode,
                onCommentsClick = component::navigateToComments
            )
        }
    ) { innerPadding ->
        if (galleryState is GalleryDetailsComponent.GalleryState.Loaded) {
            GalleryPagesLazyGrid(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                gallery = galleryState.gallery,
                pages = galleryState.pages,
                tags = galleryState.tags,
                related = galleryState.related,
                gridMode = model.gridMode,
                onPageClick = onPageClick,
                onRelatedGalleryClick = onRelatedGalleryClick,
                onTagClick = onTagClick,
                onCopyMetadata = onCopyMetadata
            )
        }
    }
}
