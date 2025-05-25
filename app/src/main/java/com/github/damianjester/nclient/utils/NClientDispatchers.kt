package com.github.damianjester.nclient.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("PropertyName")
class NClientDispatchers(
    val Default: CoroutineDispatcher = Dispatchers.Default,
    val Main: CoroutineDispatcher = Dispatchers.Main,
    val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined,
    val IO: CoroutineDispatcher = Dispatchers.IO
)
