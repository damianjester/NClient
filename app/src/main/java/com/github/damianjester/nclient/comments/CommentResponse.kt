package com.github.damianjester.nclient.comments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
