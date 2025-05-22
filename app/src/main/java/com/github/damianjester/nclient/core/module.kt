package com.github.damianjester.nclient.core

import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single { DefaultGalleryPageLoader(get(), get(), get()) } bind GalleryPageLoader::class
    single { DefaultGallerySearcher(get()) } bind GallerySearcher::class
}
