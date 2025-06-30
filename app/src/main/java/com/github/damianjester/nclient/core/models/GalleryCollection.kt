package com.github.damianjester.nclient.core.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class GalleryCollectionId(val value: Long) {
    companion object {
        const val FAVORITES_ID = 1L
    }
}

data class GalleryCollection(
    val id: GalleryCollectionId,
    val name: String,
    val lastUpdated: Instant,
    val thumbnails: List<GalleryImage.Remote>,
    val size: Int,
) {
    val isFavorites: Boolean
        get() = id.value == GalleryCollectionId.FAVORITES_ID
}
