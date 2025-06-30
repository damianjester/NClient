package com.github.damianjester.nclient.ui.gallery.collections.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.gallery.collections.details.topbar.TopAppBar
import com.github.damianjester.nclient.ui.preview.PreviewData
import com.github.damianjester.nclient.ui.preview.PreviewScreen
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun CollectionDetailsRootContent(
    component: CollectionDetailsComponent,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                component = component.topAppBar,
            )
        }
    ) { padding ->
        Content(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            component = component,
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    component: CollectionDetailsComponent,
) {
    GalleryCollectionLazyGrid(
        modifier = modifier,
        flow = component.pagingData,
        onGalleryClick = component::navigateToGallery,
        onAddToCollectionClick = component::activateAddToCollectionDialog,
        onRemoveFromCollectionClick = component::removeFromCollection
    )
}

@PreviewScreen
@Composable
private fun CollectionDetailsRootContentPreview() {
    NClientPreviewTheme {
        CollectionDetailsRootContent(
            component = PreviewCollectionDetailsComponent(PreviewData.summaries),
        )
    }
}
