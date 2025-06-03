package com.github.damianjester.nclient.ui.gallery.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.InternalError
import com.github.damianjester.nclient.ui.common.NetworkConnectionError
import com.github.damianjester.nclient.ui.gallery.search.GallerySearchComponent.GalleriesState

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    state: GalleriesState.Error,
) {
    when (state) {
        GalleriesState.Error.Internal -> InternalError(modifier)
        GalleriesState.Error.NetworkConnection -> NetworkConnectionError(modifier)
    }
}
