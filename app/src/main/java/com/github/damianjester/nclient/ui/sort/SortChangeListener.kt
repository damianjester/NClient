package com.github.damianjester.nclient.ui.sort

import com.github.damianjester.nclient.core.models.SortOrder

interface SortChangeListener<T> {
    fun onSortChanged(type: T, order: SortOrder)
}
