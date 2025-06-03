package com.github.damianjester.nclient.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.GalleryId

@Composable
fun ErrorText(
    modifier: Modifier = Modifier,
    title: String,
    body: String
) {
    Column(
        modifier
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            body,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InternalError(modifier: Modifier = Modifier) {
    ErrorText(
        modifier = modifier,
        title = stringResource(R.string.internal_error_title),
        body = stringResource(R.string.internal_error_body)
    )
}

@Composable
fun NetworkConnectionError(modifier: Modifier = Modifier) {
    ErrorText(
        modifier = modifier,
        title = stringResource(R.string.network_connection_error_title),
        body = stringResource(R.string.network_connection_error_body)
    )
}

@Composable
fun GalleryNotFound(
    modifier: Modifier = Modifier,
    id: GalleryId
) {
    ErrorText(
        modifier = modifier,
        title = stringResource(R.string.gallery_not_found_title),
        body = stringResource(R.string.gallery_not_found_body, id.value)
    )
}
