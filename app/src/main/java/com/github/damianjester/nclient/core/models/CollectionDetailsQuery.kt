package com.github.damianjester.nclient.core.models

import kotlinx.serialization.Serializable

@Serializable
data class CollectionDetailsQuery(
    val id: GalleryCollectionId,
    val sort: Sort = Sort(SortType.Added),
) {
    @Serializable
    data class Sort(val type: SortType, val order: SortOrder = SortOrder.DESC)

    @Serializable
    enum class SortType {
        Added, Title, ID
    }
}
