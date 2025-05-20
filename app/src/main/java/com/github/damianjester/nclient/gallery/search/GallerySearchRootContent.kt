package com.github.damianjester.nclient.gallery.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun GallerySearchRootContent(
    component: GallerySearchComponent,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        Text(
            "Gallery Search",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
