package com.github.damianjester.nclient

import android.app.Application
import com.github.damianjester.nclient.core.coreModule
import com.github.damianjester.nclient.db.dbModule
import com.github.damianjester.nclient.net.httpModule
import com.github.damianjester.nclient.net.imageModule
import com.github.damianjester.nclient.repo.repoModule
import com.github.damianjester.nclient.ui.uiModule
import com.github.damianjester.nclient.utils.coroutineModule
import com.github.damianjester.nclient.utils.logger.loggerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun Application.setupNClientKoin() {
    startKoin {
        androidContext(this@setupNClientKoin)
        modules(coroutineModule, loggerModule, httpModule, dbModule, repoModule, imageModule, coreModule, uiModule)
    }
}
