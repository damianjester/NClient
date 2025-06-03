package com.github.damianjester.nclient.net

sealed class NHentaiClientException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

class NHentaiClientConnectionException(cause: Throwable) : NHentaiClientException(cause)

class NHentaiClientScrapeException : NHentaiClientException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

class NHentaiClientSerializationException : NHentaiClientException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

fun scrapeError(message: String): Nothing = throw NHentaiClientScrapeException(message)
