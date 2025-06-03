package com.github.damianjester.nclient.repo

import org.koin.dsl.bind
import org.koin.dsl.module

val repoModule = module {

    single { SqlDelightGalleryRepository(get(), get(), get()) } bind GalleryRepository::class

    single { SqlDelightCommentRepository(get(), get(), get()) } bind CommentRepository::class
}
