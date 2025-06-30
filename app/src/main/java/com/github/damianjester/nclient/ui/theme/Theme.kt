package com.github.damianjester.nclient.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.LocalAsyncImagePreviewHandler
import com.github.damianjester.nclient.ui.preview.NClientAsyncImagePreviewHandler

@Composable
fun NClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
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
    NClientTheme(
        darkTheme = darkTheme
    ) {
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides NClientAsyncImagePreviewHandler(LocalContext.current)
        ) {
            content()
        }
    }
}
