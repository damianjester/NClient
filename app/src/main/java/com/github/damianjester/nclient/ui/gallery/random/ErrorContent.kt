package com.github.damianjester.nclient.ui.gallery.random

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.InternalError
import com.github.damianjester.nclient.ui.common.NetworkConnectionError
import com.github.damianjester.nclient.ui.gallery.random.RandomGalleryComponent.RandomGalleryState

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    state: RandomGalleryState.Error
) {
    when (state) {
        RandomGalleryState.Error.Internal -> InternalError(modifier)
        RandomGalleryState.Error.NetworkConnection -> NetworkConnectionError(modifier)
    }
}
