package com.github.damianjester.nclient.ui.gallery.comments

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.ui.common.InternalError
import com.github.damianjester.nclient.ui.common.NetworkConnectionError
import com.github.damianjester.nclient.ui.gallery.comments.CommentsComponent.CommentsState
import com.github.damianjester.nclient.ui.gallery.common.AsciiEmojiMessage

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
    AsciiEmojiMessage(
        stringResource(R.string.no_comments),
        modifier = modifier
    )
}
