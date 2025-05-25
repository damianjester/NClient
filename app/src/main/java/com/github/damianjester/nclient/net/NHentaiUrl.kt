package com.github.damianjester.nclient.net

import io.ktor.http.Url

object NHentaiUrl {

    const val HOST = "nhentai.net"
    const val PROTOCOL = "https"
    const val BLANK_AVATAR_PATH = "avatars/blank.png"

    fun avatarUrl(path: String): Url? {
        return (if (path == BLANK_AVATAR_PATH) null else "$PROTOCOL://i1.$HOST/$path")
            ?.let { Url(it) }
    }

    fun commentsUrl(galleryId: Long): String {
       return "$PROTOCOL://$HOST/api/gallery/$galleryId/comments"
    }

}
