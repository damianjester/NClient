package com.github.damianjester.nclient.utils.logger

interface Logger {
    fun v(tag: LogTag, message: String, throwable: Throwable? = null)

    fun d(tag: LogTag, message: String, throwable: Throwable? = null)

    fun i(tag: LogTag, message: String, throwable: Throwable? = null)

    fun w(tag: LogTag, message: String, throwable: Throwable? = null)

    fun e(tag: LogTag, message: String, throwable: Throwable? = null)
}
