package com.github.damianjester.nclient.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.damianjester.nclient.ui.common.PlaceholderDrawerRootContent

@Composable
fun SettingsRootContent(
    component: SettingsComponent,
    onDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderDrawerRootContent(
        modifier = modifier,
        onDrawerClick = onDrawerClick,
        topAppBarTitle = "Settings"
    )
}
