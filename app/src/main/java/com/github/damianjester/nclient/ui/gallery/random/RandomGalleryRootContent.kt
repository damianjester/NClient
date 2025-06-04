package com.github.damianjester.nclient.ui.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.PlaceholderDrawerRootContent
import com.github.damianjester.nclient.ui.gallery.random.RandomGalleryComponent

@Composable
fun RandomGalleryRootContent(
    component: RandomGalleryComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderDrawerRootContent(
        modifier = modifier,
        onDrawerClick = onDrawerClick,
        topAppBarTitle = "Random Gallery"
    )
}
