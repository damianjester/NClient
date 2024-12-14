package com.github.damianjester.nclient

object NHentaiUrl {

    const val HOST = "nhentai.net"
    const val PROTOCOL = "https"
    const val BLANK_AVATAR_PATH = "avatars/blank.png"

    fun avatarUrl(path: String): String? {
        return if (path == BLANK_AVATAR_PATH) null else "$PROTOCOL://i.$HOST/$path"
    }

    fun commentsUrl(galleryId: Long): String {
       return "$PROTOCOL://$HOST/api/gallery/$galleryId/comments"
    }

}
