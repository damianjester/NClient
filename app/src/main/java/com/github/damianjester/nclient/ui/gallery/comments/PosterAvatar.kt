package com.github.damianjester.nclient.ui.gallery.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import io.ktor.http.Url

@Composable
fun PosterAvatar(
    modifier: Modifier = Modifier,
    url: Url?,
) {
    if (url != null) {
        var isError by remember { mutableStateOf(false) }

        if (isError) {
            BlankAvatar()
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url.toString())
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                onState = { state ->
                    if (state is AsyncImagePainter.State.Error) {
                        isError = true
                    }
                },
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .clip(CircleShape)
                    .size(64.dp),
            )
        }
    } else {
        BlankAvatar()
    }
}

@Composable
fun BlankAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(64.dp)
            .background(MaterialTheme.colorScheme.background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Preview
@Composable
private fun PosterAvatarPreview() {
    NClientPreviewTheme {
        PosterAvatar(url = Url("https://example.com"))
    }
}

@PreviewLightDark
@Composable
private fun BlankAvatarPreview() {
    NClientPreviewTheme {
        BlankAvatar()
    }
}
