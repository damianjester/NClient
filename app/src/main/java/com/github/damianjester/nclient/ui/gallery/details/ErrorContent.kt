package com.github.damianjester.nclient.ui.gallery.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.GalleryNotFound
import com.github.damianjester.nclient.ui.common.InternalError
import com.github.damianjester.nclient.ui.common.NetworkConnectionError
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsComponent.GalleryState

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    error: GalleryState.Error,
) {
    when (error) {
        GalleryState.Error.Internal -> InternalError(modifier)
        GalleryState.Error.NetworkConnection -> NetworkConnectionError(modifier)
        is GalleryState.Error.NotFound -> GalleryNotFound(modifier, error.id)
    }
}
