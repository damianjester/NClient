package com.github.damianjester.nclient.ui.gallery.collections.rename

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.gallery.collections.CollectionNameTextField
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RenameCollectionDialog(
    component: RenameCollectionComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()

    BasicAlertDialog(
        onDismissRequest = component::dismiss,
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.dialog_rename_collection_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Spacer(Modifier.size(16.dp))
                CollectionNameTextField(
                    name = model.name,
                    onNameChanged = component::onNameChange,
                    onDone = { component.confirm() },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(Modifier.size(16.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(component::dismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = component::confirm,
                        enabled = model.submittable
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun RenameCollectionDialogPreview() {
    NClientPreviewTheme {
        RenameCollectionDialog(
            component = PreviewRenameCollectionComponent("FEET TO DIE FOR \uD83D\uDE4F")
        )
    }
}
