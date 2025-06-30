package com.github.damianjester.nclient.ui.gallery.collections.details.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopAppBar(
    component: TopAppBarComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()
    var dropdownExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {
            Text(model.collectionName ?: "")
        },
        navigationIcon = {
            IconButton(component::navigateBack) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(component::activateSortDialog) {
                Icon(
                    Icons.AutoMirrored.Default.Sort,
                    contentDescription = stringResource(R.string.sort_galleries)
                )
            }
            if (!model.isFavorite) {
                IconButton({ dropdownExpanded = !dropdownExpanded }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.dropdown_menu)
                    )
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.rename)) },
                        onClick = {
                            component.activateRenameDialog()
                            dropdownExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = {
                            component.activateDeleteDialog()
                            dropdownExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    )
}
