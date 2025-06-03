package com.github.damianjester.nclient.core.models

data class GalleryPage(
    val index: Int,
    val image: GalleryPageImages,
    val resolution: Resolution,
)

sealed interface GalleryPageImages {
    val remoteThumbnail: GalleryImage.Remote
    val remoteOriginal: GalleryImage.Remote

    data class Remote(
        override val remoteThumbnail: GalleryImage.Remote,
        override val remoteOriginal: GalleryImage.Remote,
    ) : GalleryPageImages

    data class Local(
        override val remoteThumbnail: GalleryImage.Remote,
        override val remoteOriginal: GalleryImage.Remote,
        val localThumbnail: GalleryImage.Local,
        val localOriginal: GalleryImage.Local,
    ) : GalleryPageImages
}
