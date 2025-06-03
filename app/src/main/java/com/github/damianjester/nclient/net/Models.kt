package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryTagId
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GallerySummary(
    val id: GalleryId,
    val title: String,
    val mediaId: Long,
    val coverThumbnailUrl: Url,
    val tagIds: List<GalleryTagId>, // Also contains language, see SpecialTagIds
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

@Serializable
data class Tag(
    val id: GalleryTagId,
    val type: String, // "tag", "language", "category", "parody", "character"
    val name: String,
    val url: String, // Actually path, e.g. /tag/big-breasts/
    val count: Int,
)

data class GallerySummariesResponse(
    val galleries: List<GallerySummary>,
)

sealed interface GalleryDetailsResponse {
    data class Success(
        val gallery: GalleryDetails,
        val coverUrl: Url?,
        val related: List<GallerySummary>,
        val isUserFavorite: Boolean,
    ) : GalleryDetailsResponse

    sealed interface Failure : GalleryDetailsResponse {
        data object NotFound : Failure
    }
}

@Serializable
data class Comment(
    val id: Long,
    val poster: Poster,
    @SerialName("post_date") val postDate: Long,
    val body: String,
)

@Serializable
data class Poster(
    val id: Long,
    val username: String,
    @SerialName("avatar_url") val avatarUrl: String,
)
