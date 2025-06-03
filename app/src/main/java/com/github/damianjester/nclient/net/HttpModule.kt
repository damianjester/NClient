package com.github.damianjester.nclient.net

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.AndroidClientEngine
import io.ktor.client.engine.android.AndroidEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.bind
import org.koin.dsl.module

val httpModule = module {

    single { AndroidClientEngine(AndroidEngineConfig()) } bind HttpClientEngine::class

    single { CustomAndroidHttpLogger(get()) } bind Logger::class

    single { NClientCookiesStorage(get()) } bind CookiesStorage::class

    single { Json { ignoreUnknownKeys = true } }

    single {
        HttpClient(get()) {
            install(UserAgent) {
                agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N)"
            }
            install(HttpCookies) {
                storage = get()
            }
            install(Logging) {
                // TODO: Disable in release builds
                logger = get()
                level = LogLevel.HEADERS
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }
            install(ContentNegotiation) {
                json(get())
            }
        }
    }

    single { ScrapperNHentaiHttpClient(get(), get(), get()) } bind NHentaiHttpClient::class
}
