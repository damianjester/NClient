package com.github.damianjester.nclient.utils.logger

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
