package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryTagId
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class ListGallery(
    val id: GalleryId,
    val title: String,
    val mediaId: Long,
    val coverThumbnailUrl: Url,
    val tagIds: List<GalleryTagId>, // Also contains language, see SpecialTagIds
)

@Serializable
data class DetailsGallery(
    val id: GalleryId,
    @SerialName("media_id") val mediaId: Long,
    val title: GalleryTitle,
    val images: GalleryImages,
    val scanlator: String,
    @SerialName("upload_date") val uploadDate: Long,
    val tags: List<GalleryTag>,
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

@Serializable
data class GalleryTag(
    val id: GalleryTagId,
    val type: String, // "tag", "language", "category", "parody", "character"
    val name: String,
    val url: String, // Actually path, e.g. /tag/big-breasts/
    val count: Int,
)

data class GalleriesResponse(
    val galleries: List<ListGallery>,
)

sealed interface GalleryResponse {
    data class Success(
        val gallery: DetailsGallery,
        val coverUrl: Url?,
        val related: List<ListGallery>,
        val isUserFavorite: Boolean,
    ) : GalleryResponse

    sealed interface Failure : GalleryResponse {
        data object NotFound : Failure
    }
}

@Serializable
data class CommentResponse(
    val id: Long,
    val poster: PosterResponse,
    @SerialName("post_date") val postDate: Long,
    val body: String,
)

@Serializable
data class PosterResponse(
    val id: Long,
    val username: String,
    @SerialName("avatar_url") val avatarUrl: String,
)
