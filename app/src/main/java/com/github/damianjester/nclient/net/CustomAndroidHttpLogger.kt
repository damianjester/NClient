package com.github.damianjester.nclient.net

import android.util.Log
import io.ktor.client.plugins.logging.Logger

/**
 * It's required since both [DEFAULT] and [ANDROID] uses SLF4J, which does nothing in Android by default
 * (prints warning about "no-operation (NOP) logger implementation").
 */
class CustomAndroidHttpLogger() : Logger {

    override fun log(message: String) {
        Log.i(logTag, message)
    }

    companion object {
        private const val logTag = "CustomAndroidHttpLogger"
    }
}
