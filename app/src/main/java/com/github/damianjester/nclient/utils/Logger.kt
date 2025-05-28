package com.github.damianjester.nclient.utils

import android.util.Log

object LogTags {
    val default = LogTag("logger") // tag:logger
    val comments = LogTag("comments") // tag:comments
    val gallery = LogTag("gallery") // tag:gallery
    val downloader = LogTag("downloader") // tag:downloader
    val saver = LogTag("saver") // tag:saver
    val http = LogTag("http") // tag:http
    val cookies = LogTag("cookie") // tag:cookie
}

@JvmInline
value class LogTag(val value: String)

interface Logger {
    fun v(tag: LogTag, message: String, throwable: Throwable? = null)

    fun d(tag: LogTag, message: String, throwable: Throwable? = null)

    fun i(tag: LogTag, message: String, throwable: Throwable? = null)

    fun w(tag: LogTag, message: String, throwable: Throwable? = null)

    fun e(tag: LogTag, message: String, throwable: Throwable? = null)
}

fun Logger.v(message: String, throwable: Throwable? = null) =
    v(LogTags.default, message, throwable)

fun Logger.d(message: String, throwable: Throwable? = null) =
    d(LogTags.default, message, throwable)

fun Logger.i(message: String, throwable: Throwable? = null) =
    i(LogTags.default, message, throwable)

fun Logger.w(message: String, throwable: Throwable? = null) =
    w(LogTags.default, message, throwable)

fun Logger.e(message: String, throwable: Throwable? = null) =
    e(LogTags.default, message, throwable)

class AndroidLogger : Logger {
    override fun v(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.v(tag.value, message, throwable)
        } else {
            Log.v(tag.value, message)
        }
    }

    override fun d(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.d(tag.value, message, throwable)
        } else {
            Log.d(tag.value, message)
        }
    }

    override fun i(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.i(tag.value, message, throwable)
        } else {
            Log.i(tag.value, message)
        }
    }

    override fun w(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.w(tag.value, message, throwable)
        } else {
            Log.w(tag.value, message)
        }
    }

    override fun e(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.e(tag.value, message, throwable)
        } else {
            Log.e(tag.value, message)
        }
    }
}
