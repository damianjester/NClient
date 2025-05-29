package com.github.damianjester.nclient.utils

import android.content.ClipboardManager
import android.content.Context

val Context.clipboardManager
    get() = getSystemService(ClipboardManager::class.java) as ClipboardManager
