package com.github.damianjester.nclient.utils.logger

@JvmInline
value class LogTag(val value: String)

object LogTags {
    val default = LogTag("logger") // tag:logger
    val comments = LogTag("comments") // tag:comments
    val gallery = LogTag("gallery") // tag:gallery
    val downloader = LogTag("downloader") // tag:downloader
    val saver = LogTag("saver") // tag:saver
    val http = LogTag("http") // tag:http
    val cookies = LogTag("cookie") // tag:cookie
    val caffeine = LogTag("caffeine") // tag:caffeine
    val deeplink = LogTag("deeplink") // tag:deeplink
    val history = LogTag("history") // tag:history
}
