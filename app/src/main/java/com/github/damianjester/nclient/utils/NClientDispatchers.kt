package com.github.damianjester.nclient.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

@Suppress("PropertyName")
class NClientDispatchers(
    val Default: CoroutineDispatcher = Dispatchers.Default,
    val Main: MainCoroutineDispatcher = Dispatchers.Main,
    val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined,
    val IO: CoroutineDispatcher = Dispatchers.IO
)
