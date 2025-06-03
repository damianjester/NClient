package com.github.damianjester.nclient.db.models

data class GalleryPageWithMediaId(
    val galleryId: Long,
    val pageIndex: Long,
    val fileExtension: String,
    val width: Long,
    val height: Long,
    val mediaId: Long,
)
