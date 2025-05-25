package com.github.damianjester.nclient.utils

import org.koin.dsl.module

val coroutineModule = module {
    single { NClientDispatchers() }
}
