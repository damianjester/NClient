package com.github.damianjester.nclient.ui.gallery.collections

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.github.damianjester.nclient.R

@Composable
fun CollectionNameTextField(
    name: String,
    onNameChanged: (String) -> Unit,
    onDone: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChanged,
        modifier = modifier,
        label = {
            Text(stringResource(R.string.text_field_collection_name_label))
        },
        placeholder = {
            Text(stringResource(R.string.text_field_collection_name_placeholder))
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Words
        ),
        keyboardActions = KeyboardActions(
            onDone = onDone
        ),
        singleLine = true
    )
}
