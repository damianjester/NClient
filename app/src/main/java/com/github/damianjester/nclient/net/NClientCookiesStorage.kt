package com.github.damianjester.nclient.net

import android.util.Log
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url

class NClientCookiesStorage : CookiesStorage {
    private val cookies = mutableMapOf<String, List<Cookie>>()

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val cookies = cookies[requestUrl.host] ?: emptyList()
        Log.i("cookie", "NClientCookiesStorage.get: $requestUrl, $cookies")
        return cookies
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        Log.i("cookie", "NClientCookiesStorage.addCookie: $requestUrl, $cookie")

        val cookiesForHost = (cookies[requestUrl.host] ?: emptyList()).toMutableList()
        val cookieIndex = cookiesForHost.indexOfFirst { it.name == cookie.name }

        if (cookieIndex > 0) {
            Log.i("cookie", "NClientCookiesStorage.addCookie: replacing cookie ${cookie.name}")
            cookiesForHost[cookieIndex] = cookie
            cookies[requestUrl.host] = cookiesForHost
        } else {
            Log.i("cookie", "NClientCookiesStorage.addCookie: appending new cookie ${cookie.name}")
            cookies[requestUrl.host] = cookiesForHost.plus(cookie)
        }
    }

    override fun close() {
        cookies.clear()
    }
}
