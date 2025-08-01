package com.github.damianjester.nclient.repo

import org.koin.dsl.bind
import org.koin.dsl.module

val repoModule = module {

    single { SqlDelightGalleryRepository(get(), get(), get()) } bind GalleryRepository::class

    single { SqlDelightCommentRepository(get(), get(), get()) } bind CommentRepository::class

    single { SqlDelightGalleryHistoryRepository(get(), get()) } bind GalleryHistoryRepository::class

    single { SqlDelightGalleryCollectionRepository(get(), get()) } bind GalleryCollectionRepository::class
}
