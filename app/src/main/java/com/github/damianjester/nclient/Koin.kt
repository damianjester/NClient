package com.github.damianjester.nclient

import android.app.Application
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.github.damianjester.nclient.settings.Global
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val httpModule = module {

    single { Global.client }

    single {
        OkHttp.create {
            preconfigured = get()
        }
    }

    single {
        HttpClient(get()) {
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
        modules(httpModule)
    }
}
