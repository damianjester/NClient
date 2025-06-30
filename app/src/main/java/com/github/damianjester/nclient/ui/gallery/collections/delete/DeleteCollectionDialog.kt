package com.github.damianjester.nclient.ui.gallery.collections.delete

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun DeleteCollectionDialog(
    component: DeleteCollectionComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()

    AlertDialog(
        onDismissRequest = component::dismiss,
        confirmButton = {
            TextButton(component::confirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(component::dismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = { Icon(Icons.Default.Delete, contentDescription = null) },
        title = { Text(stringResource(R.string.delete_collection_title)) },
        text = {
            Text(
                buildAnnotatedString {
                    append(stringResource(R.string.delete_collection_body_1, model.name))
                    append("\n\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.delete_collection_body_2))
                    }
                }
            )
        },
    )
}

@PreviewLightDark
@Composable
private fun DeleteCollectionDialogPreview() {
    NClientPreviewTheme {
        DeleteCollectionDialog(
            component = PreviewDeleteCollectionComponent()
        )
    }
}
