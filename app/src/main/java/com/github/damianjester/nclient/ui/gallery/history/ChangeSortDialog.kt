package com.github.damianjester.nclient.ui.gallery.history

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery.SortType
import com.github.damianjester.nclient.core.models.SortOrder

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChangeSortDialog(
    modifier: Modifier = Modifier,
    initialSort: GalleryHistoryQuery.Sort,
    onDismissRequest: () -> Unit,
    onConfirm: (SortType, SortOrder) -> Unit,
) {
    var selectedSortType by remember { mutableStateOf(initialSort.type) }
    var selectedSortOrder by remember { mutableStateOf(initialSort.order) }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
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
                            text = stringResource(R.string.sort_history_title),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Spacer(Modifier.size(16.dp))
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState()),
                    ) {
                        RadioColumn(Modifier.padding(horizontal = 8.dp)) {
                            SortType.entries.forEach { type ->

                                val text = when (type) {
                                    SortType.ViewCount -> stringResource(R.string.sort_type_views)
                                    SortType.LastVisit -> stringResource(R.string.sort_type_date)
                                }

                                RadioRow(
                                    selected = type == selectedSortType,
                                    onClick = { selectedSortType = type },
                                    text = text
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
                                    selected = order == selectedSortOrder,
                                    onClick = { selectedSortOrder = order },
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
                        TextButton(onDismissRequest) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton({ onConfirm(selectedSortType, selectedSortOrder) }) {
                            Text(stringResource(R.string.confirm))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun RadioColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.selectableGroup(), content = content)
}

@Composable
fun RadioRow(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null recommended for accessibility with screen readers
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
