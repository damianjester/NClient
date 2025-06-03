package com.github.damianjester.nclient.ui.gallery.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryPage
import com.github.damianjester.nclient.core.models.GalleryTag
import com.github.damianjester.nclient.ui.common.LoadingContent
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsComponent.GalleryState
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsComponent.MetadataCopy

@Composable
fun GalleryDetailsRootContent(
    component: GalleryDetailsComponent,
    modifier: Modifier = Modifier,
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
    onCopyMetadata: (MetadataCopy) -> Unit,
) {
    val model by component.model.subscribeAsState()
    val galleryState = model.gallery

    Scaffold(
        modifier = modifier,
        topBar = {
            val galleryTitle = when (galleryState) {
                is GalleryState.Loaded -> galleryState.details.gallery.title.pretty
                else -> ""
            }

            GalleryDetailsTopAppBar(
                galleryTitle = galleryTitle,
                isGalleryFavorite = model.isFavorite,
                onBackClick = onBack,
                onTitleLongClick = {
                    onCopyMetadata(MetadataCopy.Title(it))
                },
                onFavoriteClick = component::setGalleryFavoriteStatus,
                gridMode = model.gridMode,
                onGridModeClick = component::toggleGridMode,
                onCommentsClick = component::navigateToComments
            )
        }
    ) { innerPadding ->

        val contentModifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()

        when (galleryState) {
            is GalleryState.Loading -> LoadingContent(contentModifier)
            is GalleryState.Error -> ErrorContent(contentModifier, galleryState)
            is GalleryState.Loaded -> {
                GalleryPagesContent(
                    modifier = contentModifier,
                    galleryState = galleryState,
                    gridMode = model.gridMode,
                    onPageClick = onPageClick,
                    onRelatedGalleryClick = onRelatedGalleryClick,
                    onTagClick = onTagClick,
                    onCopyMetadata = onCopyMetadata
                )
            }
        }
    }
}
