@file:Suppress("unused")

package com.github.damianjester.nclient.utils.logger

fun Logger.v(message: String, throwable: Throwable? = null) =
    v(LogTags.default, message, throwable)

fun Logger.v(tag: LogTag, message: () -> String) =
    v(tag, message())

fun Logger.v(message: () -> String) =
    v(LogTags.default, message())

fun Logger.d(message: String, throwable: Throwable? = null) =
    d(LogTags.default, message, throwable)

fun Logger.d(tag: LogTag, message: () -> String) =
    d(tag, message())

fun Logger.d(message: () -> String) =
    d(LogTags.default, message())

fun Logger.i(message: String, throwable: Throwable? = null) =
    i(LogTags.default, message, throwable)

fun Logger.i(tag: LogTag, message: () -> String) =
    i(tag, message())

fun Logger.i(message: () -> String) =
    i(LogTags.default, message())

fun Logger.w(message: String, throwable: Throwable? = null) =
    w(LogTags.default, message, throwable)

fun Logger.w(tag: LogTag, message: () -> String) =
    w(tag, message())

fun Logger.w(message: () -> String) =
    w(LogTags.default, message())

fun Logger.e(message: String, throwable: Throwable? = null) =
    e(LogTags.default, message, throwable)

fun Logger.e(tag: LogTag, message: () -> String) =
    e(tag, message())

fun Logger.e(message: () -> String) =
    e(LogTags.default, message())
