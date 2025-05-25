package com.github.damianjester.nclient.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.github.damianjester.nclient.Database
import org.koin.dsl.bind
import org.koin.dsl.module

val dbModule = module {

    single(createdAtStart = true) {
        AndroidSqliteDriver(Database.Schema, get(), "test.db")
    } bind SqlDriver::class

    single(createdAtStart = true) {
        Database(get())
    }

    single { SqlDelightGalleryRepository(get(), get()) } bind GalleryRepository::class

    single { SqlDelightTagRepository(get(), get()) } bind TagRepository::class

    single { SqlDelightCommentRepository(get(), get()) } bind CommentRepository::class

}
