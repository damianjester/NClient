package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.utils.LogTags
import io.ktor.client.plugins.logging.Logger

/**
 * It's required since both [DEFAULT] and [ANDROID] uses SLF4J, which does nothing in Android by default
 * (prints warning about "no-operation (NOP) logger implementation").
 */
class CustomAndroidHttpLogger(
    private val logger: com.github.damianjester.nclient.utils.Logger
) : Logger {
    override fun log(message: String) {
        logger.i(LogTags.http, message)
    }
}
