package com.github.damianjester.nclient.net.models

import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryTagId
import io.ktor.http.Url

data class GallerySummariesResponse(
    val galleries: List<GallerySummary>,
)

data class GallerySummary(
    val id: GalleryId,
    val title: String,
    val mediaId: Long,
    val coverThumbnailUrl: Url,
    val tagIds: List<GalleryTagId>, // Also contains language, see SpecialTagIds
)
