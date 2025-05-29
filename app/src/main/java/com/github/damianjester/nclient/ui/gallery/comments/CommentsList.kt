package com.github.damianjester.nclient.ui.gallery.comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun CommentsList(
    modifier: Modifier = Modifier,
    comments: List<Comment>,
) {
    LazyColumn(
        modifier = modifier,
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
                    Text(comment.body)
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
        CommentsList(
            comments = comments
        )
    }
}
