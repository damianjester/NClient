package com.github.damianjester.nclient.net

import coil3.network.NetworkFetcher
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import org.koin.dsl.bind
import org.koin.dsl.module

val imageModule = module {
    single {
        KtorNetworkFetcherFactory(get<HttpClient>())
    } bind NetworkFetcher.Factory::class
}

