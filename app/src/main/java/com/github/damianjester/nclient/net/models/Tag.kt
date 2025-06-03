package com.github.damianjester.nclient.net.models

import com.github.damianjester.nclient.core.models.GalleryTagId
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: GalleryTagId,
    val type: String, // "tag", "language", "category", "parody", "character"
    val name: String,
    val url: String, // Actually path, e.g. /tag/big-breasts/
    val count: Int,
)

