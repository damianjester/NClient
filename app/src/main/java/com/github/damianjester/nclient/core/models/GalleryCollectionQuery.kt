package com.github.damianjester.nclient.core.models

import kotlinx.serialization.Serializable

@Serializable
data class GalleryCollectionQuery(
    val sort: Sort,
) {
    @Serializable
    data class Sort(val type: SortType, val order: SortOrder = SortOrder.DESC)

    @Serializable
    enum class SortType {
        ID, UpdatedAt, Name, Size
    }

    companion object {
        val Default: GalleryCollectionQuery
            get() = GalleryCollectionQuery(sort = Sort(SortType.UpdatedAt))
    }
}
