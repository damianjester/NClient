package com.github.damianjester.nclient.ui.sort

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> SortDialog(
    component: SortDialogComponent<T>,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()

    BasicAlertDialog(
        onDismissRequest = component::dismiss,
        content = {
            Surface(
                modifier = modifier
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
                            Icons.AutoMirrored.Default.Sort,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = model.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Spacer(Modifier.size(16.dp))
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState()),
                    ) {
                        RadioColumn(Modifier.padding(horizontal = 8.dp)) {
                            model.options.forEach { option ->
                                RadioRow(
                                    selected = option.type == model.selected,
                                    onClick = { component.select(option.type) },
                                    text = option.label
                                )
                            }
                        }
                        HorizontalDivider(Modifier.padding(horizontal = 24.dp))
                        RadioColumn(Modifier.padding(horizontal = 8.dp)) {
                            SortOrder.entries.forEach { order ->

                                val text = when (order) {
                                    SortOrder.ASC -> stringResource(R.string.sort_order_ascending)
                                    SortOrder.DESC -> stringResource(R.string.sort_order_descending)
                                }

                                RadioRow(
                                    selected = order == model.order,
                                    onClick = { component.order(order) },
                                    text = text
                                )
                            }
                        }
                    }
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
                        TextButton(component::confirm) {
                            Text(stringResource(R.string.confirm))
                        }
                    }
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun SortDialogPreview() {
    NClientPreviewTheme {
        SortDialog(
            component = PreviewSortDialogComponent(
                options = setOf(
                    SortOption("Option 1", "1"),
                    SortOption("Option 2", "2"),
                    SortOption("Option 3", "3"),
                ),
                selected = "2"
            )
        )
    }
}
