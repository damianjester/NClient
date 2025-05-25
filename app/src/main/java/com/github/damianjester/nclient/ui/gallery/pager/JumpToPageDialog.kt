package com.github.damianjester.nclient.ui.gallery.pager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.damianjester.nclient.R
import kotlinx.coroutines.launch

@Composable
fun JumpToPageDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
) {
    Dialog(
        onDismissRequest
    ) {
        val scope = rememberCoroutineScope()

        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                Modifier.padding(16.dp)
            ) {

                Text(
                    stringResource(R.string.jump_to_page),
                    style = MaterialTheme.typography.h6
                )

                Spacer(Modifier.height(24.dp))

                var textFieldValue by remember {
                    mutableStateOf((pagerState.currentPage + 1).toString())
                }

                val isError = textFieldValue.toIntOrNull()
                    ?.let { it < 1 || it > pagerState.pageCount }
                    ?: true

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton({
                        textFieldValue.toIntOrNull()
                            ?.let { (it - 1).coerceIn(1, pagerState.pageCount) }
                            ?.let { textFieldValue = it.toString() }
                    }) {
                        Icon(Icons.Default.ChevronLeft, null)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = textFieldValue,
                            onValueChange = { value ->
                                val numberValue = value.toIntOrNull()
                                if (numberValue != null && numberValue > 0) {
                                    textFieldValue = value
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            isError = isError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "/",
                            fontSize = 32.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(pagerState.pageCount.toString())
                    }

                    IconButton(
                        {
                            textFieldValue.toIntOrNull()
                                ?.let { (it + 1).coerceIn(1, pagerState.pageCount) }
                                ?.let { textFieldValue = it.toString() }
                        }
                    ) {
                        Icon(Icons.Default.ChevronRight, null)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row {
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onDismissRequest) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(Modifier.width(16.dp))
                    Button({
                        scope.launch {
                            textFieldValue.toIntOrNull()
                                ?.let { page ->
                                    pagerState.scrollToPage(page - 1)
                                }
                            onDismissRequest()
                        }
                    },
                        enabled = !isError
                    ) {
                        Text(stringResource(R.string.jump))
                    }
                }
            }
        }
    }
}
