package com.github.damianjester.nclient.ui.gallery.collections.add.selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.ui.gallery.collections.CollectionImage
import com.github.damianjester.nclient.ui.gallery.collections.add.selection.CollectionSelectionComponent.CollectionsState
import com.github.damianjester.nclient.ui.preview.PreviewData
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
fun CollectionSelectionContent(
    component: CollectionSelectionComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()
    val state = model.state

    when (state) {
        CollectionsState.Loading -> Unit
        is CollectionsState.Loaded -> {
            if (state.collections.isEmpty()) {
                Box(modifier) {
                    Text(
                        stringResource(R.string.no_collections),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = modifier,
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val itemModifier = Modifier.fillMaxWidth()

                    items(state.collections, key = { it.id.value }) { collection ->
                        CollectionCheckboxRow(
                            modifier = itemModifier,
                            collection = collection,
                            selection = model.selection,
                            onSelectChange = component::changeSelection
                        )
                    }
                    item {
                        OutlinedButton(
                            component::navigateCreateCollection,
                            modifier = itemModifier
                        ) {
                            Text(stringResource(R.string.button_new_collection_label))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionCheckboxRow(
    modifier: Modifier = Modifier,
    collection: GalleryCollection,
    selection: List<GalleryCollectionId>,
    onSelectChange: (GalleryCollectionId) -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onSelectChange(collection.id) }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CollectionImage(
            modifier = Modifier.size(64.dp),
            collection = collection,
        )

        Spacer(Modifier.size(16.dp))

        Text(
            collection.name,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )

        Checkbox(
            checked = selection.contains(collection.id),
            onCheckedChange = null
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun CollectionSelectionContentPreview() {
    NClientPreviewTheme {
        CollectionSelectionContent(
            component = PreviewCollectionSelectionComponent(PreviewData.collections)
        )
    }
}
