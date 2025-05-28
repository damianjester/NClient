package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import com.github.damianjester.nclient.utils.logger.i
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ScreenCaffeinater {
    val keepScreenOn: StateFlow<Boolean>

    fun caffinateScreen(value: Boolean)
}

class DefaultScreenCaffeinater(
    private val logger: Logger,
) : ScreenCaffeinater {
    private val _keepScreenOn = MutableStateFlow(false)
    override val keepScreenOn: StateFlow<Boolean> = _keepScreenOn

    override fun caffinateScreen(value: Boolean) {
        logger.i(LogTags.caffeine, "caffeinated screen state changed: $value.")
        _keepScreenOn.value = value
    }
}
