package com.github.damianjester.nclient.ui.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.PlaceholderDrawerRootContent

@Composable
fun AboutRootContent(
    component: AboutComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderDrawerRootContent(
        modifier = modifier,
        onDrawerClick = onDrawerClick,
        topAppBarTitle = "About"
    )
}
