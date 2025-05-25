package com.github.damianjester.nclient.core

import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single { DefaultGallerySearchPager(get(), get(), get()) } bind GallerySearchPager::class
    single { DefaultGallerySearchFetcher(get()) } bind GallerySearchFetcher::class

    single { DefaultGalleryDetailsLoader(get(), get(), get()) } bind GalleryDetailsLoader::class
    single { DefaultGalleryFetcher(get()) } bind GalleryFetcher::class
    single { DefaultGalleryPagesFetcher(get()) } bind GalleryPagesFetcher::class
    single { DefaultGalleryTagsFetcher(get()) } bind GalleryTagsFetcher::class

    single { DefaultGalleryPageDownloader(get(), get(), get()) } bind GalleryPageDownloader::class
    single { DefaultGalleryPageSaver(get(), get(), get(), get()) } bind GalleryPageSaver::class
    single { DefaultGalleryPageSharer(get(), get(), get()) } bind GalleryPageSharer::class

    single { DefaultGalleryCommentsFetcher(get(), get()) } bind GalleryCommentsFetcher::class
    single { DefaultGalleryCommentsObserver(get(), get()) } bind GalleryCommentsObserver::class

}
