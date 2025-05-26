package com.github.damianjester.nclient.utils

import org.koin.dsl.bind
import org.koin.dsl.module

val loggerModule = module {
    single { AndroidLogger() } bind Logger::class
}
