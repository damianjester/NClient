package com.github.damianjester.nclient.net.models
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class CommentsResponse(val comments: List<Comment>)

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
