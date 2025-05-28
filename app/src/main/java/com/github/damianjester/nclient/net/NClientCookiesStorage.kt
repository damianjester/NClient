package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.utils.LogTags
import com.github.damianjester.nclient.utils.Logger
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url

class NClientCookiesStorage(
    private val logger: Logger,
) : CookiesStorage {
    private val cookies = mutableMapOf<String, List<Cookie>>() // TODO: Persist to db

    override suspend fun get(requestUrl: Url): List<Cookie> =
        cookies[requestUrl.host] ?: emptyList()

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        val cookiesForHost = (cookies[requestUrl.host] ?: emptyList()).toMutableList()
        val cookieIndex = cookiesForHost.indexOfFirst { it.name == cookie.name }

        if (cookieIndex > 0) {
            logger.i(
                LogTags.cookies,
                "Replacing existing cookie ${cookie.name} for ${requestUrl.host}."
            )
            cookiesForHost[cookieIndex] = cookie
            cookies[requestUrl.host] = cookiesForHost
        } else {
            logger.i(LogTags.cookies, "Appending new cookie ${cookie.name} for ${requestUrl.host}.")
            cookies[requestUrl.host] = cookiesForHost.plus(cookie)
        }
    }

    override fun close() {
        cookies.clear()
    }
}
