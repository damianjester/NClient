package com.github.damianjester.nclient.core

import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single { DefaultGalleryPager(get(), get(), get()) } bind GalleryPager::class
    single { DefaultGalleriesFetcher(get()) } bind GalleriesFetcher::class
}
