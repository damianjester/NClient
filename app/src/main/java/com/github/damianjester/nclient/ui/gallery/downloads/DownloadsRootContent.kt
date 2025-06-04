package com.github.damianjester.nclient.ui.gallery.downloads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.PlaceholderDrawerRootContent

@Composable
fun DownloadsRootContent(
    component: DownloadsComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderDrawerRootContent(
        modifier = modifier,
        onDrawerClick = onDrawerClick,
        topAppBarTitle = "Downloads"
    )
}
