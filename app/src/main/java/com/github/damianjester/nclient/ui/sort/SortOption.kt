package com.github.damianjester.nclient.ui.sort

import kotlinx.serialization.Serializable

@Serializable
data class SortOption<T>(val label: String, val type: T)
