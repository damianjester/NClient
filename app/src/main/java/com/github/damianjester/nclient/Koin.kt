package com.github.damianjester.nclient

import android.app.Application
import android.util.Log
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.github.damianjester.nclient.core.coreModule
import com.github.damianjester.nclient.db.dbModule
import com.github.damianjester.nclient.net.NHentaiHttpClient
import com.github.damianjester.nclient.net.ScrapperNHentaiHttpClient
import com.github.damianjester.nclient.settings.Global
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.AndroidClientEngine
import io.ktor.client.engine.android.AndroidEngineConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * It's required since both [DEFAULT] and [ANDROID] uses SLF4J, which does nothing in Android by default
 * (prints warning about "no-operation (NOP) logger implementation").
 */
private object CustomAndroidHttpLogger : Logger {
    private const val logTag = "CustomAndroidHttpLogger"

    override fun log(message: String) {
        Log.i(logTag, message)
    }
}

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

val httpModule = module {

    single { NClientDispatchers() }

    single { Global.client }

    single(qualifier = named("okhttp")) {
        OkHttp.create {
            preconfigured = get()
        }
    } bind HttpClientEngine::class

    single(qualifier = named("android")) {
        AndroidClientEngine(AndroidEngineConfig())
    } bind HttpClientEngine::class

    single {
        HttpClient(get(qualifier = named("android"))) {
            install(UserAgent) {
                agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N)"
            }
            install(HttpCookies) {
                storage = NClientCookiesStorage()
            }
            install(Logging) {
                // TODO: Disable in release builds
                logger = CustomAndroidHttpLogger
                level = LogLevel.HEADERS
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }
            install(ContentNegotiation) {
                json(
                    json = Json { ignoreUnknownKeys = true }
                )
            }
        }
    }

    single {
        OkHttpNetworkFetcherFactory(
            callFactory = { get<OkHttpClient>() }
        )
    }

    single {
        ScrapperNHentaiHttpClient(get())
    } bind NHentaiHttpClient::class

}

fun Application.setupNClientKoin() {
    startKoin {
        androidContext(this@setupNClientKoin)
        modules(httpModule, coreModule, dbModule)
    }
}
