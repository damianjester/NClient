package com.github.damianjester.nclient.ui.drawer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun NClientDrawerHeader(modifier: Modifier = Modifier) {
    Row(
        modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val resId = if (isSystemInDarkTheme()) R.drawable.ic_logo else R.drawable.ic_logo_dark

        Icon(
            painterResource(resId),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.size(8.dp))
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@PreviewLightDark
@Composable
private fun NClientDrawerHeaderPreview() {
    NClientPreviewTheme {
        Surface {
            Box(Modifier.padding(16.dp)) {
                NClientDrawerHeader(Modifier.width(400.dp))
            }
        }
    }
}
