package com.github.damianjester.nclient.ui.gallery.collections.add.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.gallery.collections.CollectionNameTextField
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun CreateCollectionContent(
    component: CreateCollectionComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
    ) {
        Text(stringResource(R.string.dialog_new_collection_name_body))
        Spacer(Modifier.size(24.dp))
        CollectionNameTextField(
            name = model.name,
            onNameChanged = component::onNewCollectionNameChanged,
            onDone = { component.onDone() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun CreateCollectionContentPreview() {
    NClientPreviewTheme {
        CreateCollectionContent(
            component = PreviewCreateCollectionComponent("Boobies!!!")
        )
    }
}
