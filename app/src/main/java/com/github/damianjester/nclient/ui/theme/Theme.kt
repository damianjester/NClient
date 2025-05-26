package com.github.damianjester.nclient.ui.theme

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.github.damianjester.nclient.R

@Composable
fun NClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
@OptIn(ExperimentalCoilApi::class)
fun NClientPreviewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {

    val previewImage =
        (if (darkTheme) R.drawable.ic_logo else R.drawable.ic_logo_dark)
            .let { res -> AppCompatResources.getDrawable(LocalContext.current, res)!! }.asImage()

    val previewHandler = AsyncImagePreviewHandler { previewImage }

    NClientTheme {
        CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
            content()
        }
    }
}
