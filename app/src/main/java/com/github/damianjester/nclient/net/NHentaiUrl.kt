package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.core.GalleryPageImageFileType
import com.github.damianjester.nclient.core.MediaId
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.path

object NHentaiUrl {
    const val HOST = "nhentai.net"
    const val PROTOCOL = "https"
    const val BLANK_AVATAR_PATH = "avatars/blank.png"
    const val GALLERIES_PATH_SEGMENT = "galleries"
    const val T1_SUBDOMAIN = "t1"
    const val I1_SUBDOMAIN = "i1"

    fun baseUrl(subdomain: String? = null): Url =
        URLBuilder()
            .apply {
                host = "$subdomain.$HOST"
                protocol = URLProtocol.HTTPS
            }
            .build()

    fun avatarUrl(path: String): Url? {
        return (if (path == BLANK_AVATAR_PATH) null else "$PROTOCOL://i1.$HOST/$path")
            ?.let { Url(it) }
    }

    /**
     * Return the following URL:
     * https://t1.nhentai.net/galleries/{mediaId}/{pageNumber}t.{fileExtension}
     */
    fun galleryPageThumbnail(
        mediaId: MediaId,
        pageNumber: Int,
        fileType: GalleryPageImageFileType,
    ): Url =
        URLBuilder(baseUrl(T1_SUBDOMAIN))
            .apply {
                val filename = "${pageNumber}t.${fileType.toThumbnailFileExtension()}"
                path(GALLERIES_PATH_SEGMENT, "${mediaId.value}", filename)
            }
            .build()

    /**
     * Return the following URL:
     * https://i1.nhentai.net/galleries/{mediaId}/{pageNumber}.{fileExtension}
     */
    fun galleryPage(
        mediaId: MediaId,
        pageNumber: Int,
        fileType: GalleryPageImageFileType,
    ): Url =
        URLBuilder(baseUrl(I1_SUBDOMAIN))
            .apply {
                val filename = "${pageNumber}.${fileType.toOriginalFileExtension()}"
                path(GALLERIES_PATH_SEGMENT, "${mediaId.value}", filename)
            }
            .build()

    fun commentsUrl(galleryId: Long): String {
        return "$PROTOCOL://$HOST/api/gallery/$galleryId/comments"
    }
}
