package com.github.damianjester.nclient.ui.gallery.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.common.InternalError
import com.github.damianjester.nclient.ui.common.NetworkConnectionError
import com.github.damianjester.nclient.ui.gallery.comments.CommentsComponent.CommentsState

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    error: CommentsState.Error,
) {
    when (error) {
        CommentsState.Error.Internal -> InternalError(modifier)
        CommentsState.Error.NetworkConnection -> NetworkConnectionError(modifier)
    }
}

@Composable
fun NoCommentsFoundContent(modifier: Modifier = Modifier) {
    Box(modifier) {
        Text(
            stringResource(R.string.no_comments),
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
        )
    }
}
