package com.github.damianjester.nclient.db.models

data class GalleryWithTagIds(
    val id: Long,
    val title: String,
    val mediaId: Long,
    val coverThumbnailFileExtension: String?,
    val tagIds: List<Long>,
)

data class GalleryHistoryWithTagIds(
    val id: Long,
    val title: String,
    val mediaId: Long,
    val coverThumbnailFileExtension: String?,
    val tagIds: List<Long>,
    val lastVisitInstant: Long,
    val visitCount: Long
)
