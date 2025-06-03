package com.github.damianjester.nclient.core

import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single { DefaultGallerySearchPager(get(), get()) } bind GallerySearchPager::class

    single { DefaultGalleryDetailsCacher(get(), get(), get()) } bind GalleryDetailsCacher::class
    single { DefaultGalleryDetailsFetcher(get(), get()) } bind GalleryDetailsFetcher::class
    single { DefaultGalleryPagesFetcher(get(), get()) } bind GalleryPagesFetcher::class

    single { DefaultGalleryPageDownloader(get(), get(), get(), get()) } bind GalleryPageDownloader::class
    single { DefaultGalleryPageSaver(get(), get(), get(), get(), get()) } bind GalleryPageSaver::class
    single { DefaultGalleryPageSharer(get(), get(), get()) } bind GalleryPageSharer::class

    single { DefaultCommentsFetcher(get(), get(), get()) } bind CommentsFetcher::class

    single { DefaultWebPageOpener(get()) } bind WebPageOpener::class
    single { DefaultScreenCaffeinater(get()) } bind ScreenCaffeinater::class
}
