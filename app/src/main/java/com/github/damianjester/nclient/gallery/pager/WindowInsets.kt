package com.github.damianjester.nclient.gallery.pager

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun rememberWindowInsetsController(): WindowInsetsControllerCompat {
    val activity = LocalView.current.context as? Activity

    return remember(activity) {
        val window = activity?.window
            ?: error("No window available to get insets controller from.")
        WindowCompat.getInsetsController(window, window.decorView)
    }
}

@SuppressLint("WrongConstant")
fun WindowInsetsControllerCompat.setSystemBarsVisibility(visible: Boolean) {
    if (visible) {
        show(WindowInsetsCompat.Type.statusBars())
        show(WindowInsetsCompat.Type.navigationBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
    } else {
        hide(WindowInsetsCompat.Type.statusBars())
        hide(WindowInsetsCompat.Type.navigationBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
