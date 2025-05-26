package com.github.damianjester.nclient.core

import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    single { DefaultGallerySearchPager(get(), get(), get()) } bind GallerySearchPager::class
    single { DefaultGallerySearchObserver(get()) } bind GallerySearchObserver::class

    single { DefaultGalleryDetailsFetcher(get(), get(), get()) } bind GalleryDetailsFetcher::class
    single { DefaultGalleryDetailsObserver(get()) } bind GalleryDetailsObserver::class
    single { DefaultGalleryPagesObserver(get()) } bind GalleryPagesObserver::class
    single { DefaultGalleryTagsFetcher(get()) } bind GalleryTagsFetcher::class

    single { DefaultGalleryPageDownloader(get(), get(), get()) } bind GalleryPageDownloader::class
    single { DefaultGalleryPageSaver(get(), get(), get(), get()) } bind GalleryPageSaver::class
    single { DefaultGalleryPageSharer(get(), get(), get()) } bind GalleryPageSharer::class

    single { DefaultCommentsFetcher(get(), get()) } bind CommentsFetcher::class
    single { DefaultCommentsObserver(get(), get()) } bind CommentsObserver::class
}
