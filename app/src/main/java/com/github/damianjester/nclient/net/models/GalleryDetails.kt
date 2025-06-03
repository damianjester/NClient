package com.github.damianjester.nclient.net.models

import com.github.damianjester.nclient.core.models.GalleryId
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GalleryDetailsResponse(
        val gallery: GalleryDetails,
        val coverUrl: Url?,
        val related: List<GallerySummary>,
        val isUserFavorite: Boolean,
)

@Serializable
data class GalleryDetails(
    val id: GalleryId,
    @SerialName("media_id") val mediaId: Long,
    val title: GalleryTitle,
    val images: GalleryImages,
    val scanlator: String,
    @SerialName("upload_date") val uploadDate: Long,
    val tags: List<Tag>,
    @SerialName("num_pages") val numPages: Int,
    @SerialName("num_favorites") val numFavorites: Int,
)

@Serializable
data class GalleryTitle(
    val english: String?,
    val japanese: String?,
    val pretty: String,
)

@Serializable
data class GalleryImages(
    val pages: List<GalleryImage>,
    val cover: GalleryImage,
    val thumbnail: GalleryImage,
)

@Serializable
data class GalleryImage(
    val t: String, // Either "w" or ???
    val w: Int,
    val h: Int,
)

