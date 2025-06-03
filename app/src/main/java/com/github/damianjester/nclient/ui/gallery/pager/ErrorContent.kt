package com.github.damianjester.nclient.ui.gallery.pager

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.GalleryNotFound
import com.github.damianjester.nclient.ui.common.InternalError
import com.github.damianjester.nclient.ui.common.NetworkConnectionError
import com.github.damianjester.nclient.ui.gallery.pager.GalleryPagerComponent.PagesState

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    error: PagesState.Error
) {
    when (error) {
        PagesState.Error.Internal -> InternalError(modifier)
        PagesState.Error.NetworkConnection -> NetworkConnectionError(modifier)
        is PagesState.Error.GalleryNotFound -> GalleryNotFound(modifier, error.id)
    }
}
