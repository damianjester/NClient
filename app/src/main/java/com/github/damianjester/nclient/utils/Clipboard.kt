package com.github.damianjester.nclient.utils

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

val Context.clipboardManager
    @RequiresApi(Build.VERSION_CODES.M)
    get() = getSystemService(ClipboardManager::class.java) as ClipboardManager

@Suppress("DEPRECATION")
val Context.legacyClipboardManager
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
