package com.github.damianjester.nclient.core

import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single { DefaultGalleryPagePager(get(), get(), get()) } bind GalleryPagePager::class
    single { DefaultGallerySearcher(get()) } bind GallerySearcher::class
}
