package com.github.damianjester.nclient.ui

import org.koin.dsl.bind
import org.koin.dsl.module

val uiModule = module {
    single { DefaultDeepLinker(get()) } bind DeepLinker::class
}
