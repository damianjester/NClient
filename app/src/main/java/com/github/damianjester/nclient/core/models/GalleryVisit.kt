package com.github.damianjester.nclient.core.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

data class GalleryVisit(
    val gallery: GallerySummary,
    val lastVisit: Instant,
    val visitCount: Int,
)

data class GalleryHistoryQuery(
    val sort: Sort = Sort(SortType.LastVisit, SortOrder.DESC)
) {
    @Serializable
    data class Sort(val type: SortType, val order: SortOrder = SortOrder.DESC)

    @Serializable
    enum class SortType {
        ViewCount, LastVisit
    }
}

@Serializable
enum class SortOrder {
    ASC, DESC
}
