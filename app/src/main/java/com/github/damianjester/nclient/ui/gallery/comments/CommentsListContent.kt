package com.github.damianjester.nclient.ui.gallery.comments

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.damianjester.nclient.core.Comment
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme

private const val MAX_COMMENT_LINES = 7

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CommentsListContent(
    modifier: Modifier = Modifier,
    comments: List<Comment>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    if (comments.isEmpty()) {
        NoCommentsFoundContent(modifier)
    } else {
        PullToRefreshBox(
            isRefreshing,
            onRefresh = onRefresh,
            modifier = modifier
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(comments, key = { it.id.value }) { comment ->
                    CommentItem(
                        modifier = Modifier.fillMaxWidth(),
                        comment = comment
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    modifier: Modifier = Modifier,
    comment: Comment,
) {
    Card(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            PosterAvatar(
                url = comment.poster.avatar
            )
            Spacer(Modifier.size(16.dp))
            Column {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = comment.poster.username,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(8.dp))
                SelectionContainer {
                    var showAll by remember { mutableStateOf(false) }

                    Text(
                        comment.body,
                        modifier = Modifier
                            .animateContentSize()
                            .clickable(
                                onClick = { showAll = !showAll },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = if (showAll) Int.MAX_VALUE else MAX_COMMENT_LINES,
                    )
                }
                Spacer(Modifier.size(8.dp))

                val context = LocalContext.current
                Text(
                    formatCommentPostDate(context, comment.elapsedTime),
                    modifier = Modifier.alpha(0.6f),
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CommentsListPreview(
    @PreviewParameter(CommentsPreviewParameterProvider::class) comments: List<Comment>,
) {
    NClientPreviewTheme {
        CommentsListContent(
            comments = comments,
            isRefreshing = false,
            onRefresh = {}
        )
    }
}
