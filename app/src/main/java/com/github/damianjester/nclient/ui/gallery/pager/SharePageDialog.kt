package com.github.damianjester.nclient.ui.gallery.pager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.damianjester.nclient.R

@Composable
fun SharePageDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onShare: (Boolean) -> Unit,
) {
    Dialog(
        onDismissRequest
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                Modifier.padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.share_page),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(24.dp))
                var includeUrl by remember { mutableStateOf(false) }
                Row(Modifier.fillMaxWidth()) {
                    Checkbox(
                        checked = includeUrl,
                        onCheckedChange = { includeUrl = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.caption_send_with_title))
                }
                Spacer(Modifier.height(24.dp))
                Row {
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onDismissRequest) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(Modifier.width(16.dp))
                    Button({ onShare(includeUrl) }) {
                        Text(stringResource(R.string.share))
                    }
                }
            }
        }
    }
}
