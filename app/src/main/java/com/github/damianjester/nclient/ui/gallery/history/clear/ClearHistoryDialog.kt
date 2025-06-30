package com.github.damianjester.nclient.ui.gallery.history.clear

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.github.damianjester.nclient.R

@Composable
fun ClearHistoryDialog(
    component: ClearHistoryComponent,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = component::dismiss,
        confirmButton = {
            TextButton(onClick = component::confirm) {
                Text(stringResource(R.string.clear))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = component::dismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(Icons.Default.ClearAll, contentDescription = null)
        },
        title = {
            Text(text = stringResource(R.string.clear_history_title))
        },
        text = {
            Text(
                buildAnnotatedString {
                    append(stringResource(R.string.clear_history_body_1))
                    append("\n\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.clear_history_body_2))
                    }
                }
            )
        },
    )
}
