package com.github.damianjester.nclient.ui.gallery.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.PlaceholderDrawerRootContent

@Composable
fun BookmarksRootContent(
    component: BookmarksComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderDrawerRootContent(
        modifier = modifier,
        onDrawerClick = onDrawerClick,
        topAppBarTitle = "Bookmarks"
    )
}
