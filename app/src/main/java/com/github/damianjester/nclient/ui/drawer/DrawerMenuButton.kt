package com.github.damianjester.nclient.ui.drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun DrawerMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            Icons.Default.Menu,
            contentDescription = stringResource(R.string.menu)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawerMenuButtonPreview() {
    NClientPreviewTheme {
        DrawerMenuButton(
            onClick = {},
        )
    }
}
