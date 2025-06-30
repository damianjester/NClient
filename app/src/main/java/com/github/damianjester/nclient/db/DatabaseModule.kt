package com.github.damianjester.nclient.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.github.damianjester.nclient.Database
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.dsl.bind
import org.koin.dsl.module

private const val NCLIENT_DB_NAME = "nclient.db"

val dbModule = module {

    single(createdAtStart = true) {
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = get(),
            name = NCLIENT_DB_NAME,
            factory = RequerySQLiteOpenHelperFactory()
        )
    } bind SqlDriver::class

    single(createdAtStart = true) {
        Database(get())
    }
}
