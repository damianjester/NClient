package com.github.damianjester.nclient.ui.gallery.common.grid

import android.os.Parcelable
import androidx.paging.compose.LazyPagingItems
import com.github.damianjester.nclient.core.models.GallerySummary
import kotlinx.parcelize.Parcelize

fun lazyPagingKey(
    index: Int,
    items: LazyPagingItems<GallerySummary>,
): LazyPagingKey = (items.peek(index)
    ?.let { LazyPagingKey.Id(it.id.value) }
    ?: LazyPagingKey.IndexAt(index))

sealed interface LazyPagingKey {
    @Parcelize
    data class Id(val id: Long) : LazyPagingKey, Parcelable

    @Parcelize
    data class IndexAt(val index: Int) : LazyPagingKey, Parcelable
}
