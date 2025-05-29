package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryImageFileType
import com.github.damianjester.nclient.core.GalleryPage
import com.github.damianjester.nclient.core.MediaId
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.appendEncodedPathSegments
import io.ktor.http.path

object NHentaiUrl {
    const val HOST = "nhentai.net"
    const val BLANK_AVATAR_PATH = "avatars/blank.png"
    const val G_PATH_SEGMENT = "g" // Gallery
    const val GALLERY_PATH_SEGMENT = "gallery"
    const val API_PATH_SEGMENT = "api"
    const val GALLERIES_PATH_SEGMENT = "galleries"
    const val T1_SUBDOMAIN = "t1"
    const val I1_SUBDOMAIN = "i1"

    fun baseUrl(subdomain: String? = null): Url =
        URLBuilder()
            .apply {
                host = "${subdomain?.let { "$it." } ?: ""}$HOST"
                protocol = URLProtocol.HTTPS
            }
            .build()

    /**
     * Return the following URL:
     * https://i1.nhentai.net/$path
     */
    fun posterAvatar(path: String): Url? {
        if (path == BLANK_AVATAR_PATH) {
            return null
        }

        return URLBuilder(baseUrl(I1_SUBDOMAIN))
            .apply { path(path) }
            .build()
    }

    /**
     * Returns:
     * https://t1.nhentai.net/galleries/[mediaId]/thumb.[fileType]
     */
    fun galleryCoverThumbnail(
        mediaId: MediaId,
        fileType: GalleryImageFileType,
    ): Url =
        URLBuilder(baseUrl(T1_SUBDOMAIN))
            .apply {
                val extension = fileType.toFileExtension()
                path(GALLERIES_PATH_SEGMENT, "${mediaId.value}", "thumb.$extension")
            }
            .build()

    /**
     * Returns:
     * https://t1.nhentai.net/galleries/[mediaId]/cover.[fileType]
     */
    fun galleryCover(
        mediaId: MediaId,
        fileType: GalleryImageFileType,
    ) =
        URLBuilder(baseUrl(T1_SUBDOMAIN))
            .apply {
                val extension = fileType.toFileExtension()
                path(GALLERIES_PATH_SEGMENT, "${mediaId.value}", "cover.$extension")
            }
            .build()

    /**
     * Return the following URL:
     * https://t1.nhentai.net/galleries/{mediaId}/{pageNumber}t.{fileExtension}
     */
    fun galleryPageThumbnail(
        mediaId: MediaId,
        pageNumber: Int,
        fileType: GalleryImageFileType,
    ): Url =
        URLBuilder(baseUrl(T1_SUBDOMAIN))
            .apply {
                val filename = "${pageNumber}t.${fileType.toFileExtension()}"
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
        fileType: GalleryImageFileType,
    ): Url =
        URLBuilder(baseUrl(I1_SUBDOMAIN))
            .apply {
                val filename = "$pageNumber.${fileType.toFileExtension()}"
                path(GALLERIES_PATH_SEGMENT, "${mediaId.value}", filename)
            }
            .build()

    /**
     * Returns:
     * https://nhentai.net/api/gallery/$galleryId/comments
     */
    fun comments(id: GalleryId): Url =
        URLBuilder(baseUrl())
            .apply {
                path(API_PATH_SEGMENT, GALLERY_PATH_SEGMENT, "${id.value}", "comments")
            }
            .build()

    /**
     * https://nhentai.net?page=$pageNumber
     */
    fun galleriesWebpage(pageNumber: Int): Url =
        URLBuilder(baseUrl())
            .apply {
                parameters.append("page", pageNumber.toString())
            }
            .build()

    /**
     * Return:
     * https://nhentai.net/g/${galleryId}/
     */
    fun galleryWebpage(id: GalleryId): Url =
        URLBuilder(baseUrl())
            .apply {
                path(G_PATH_SEGMENT, "${id.value}/")
            }
            .build()

    /**
     * Returns:
     * https://nhentai.net/g/$galleryId/$pageNumber/
     */
    fun galleryPageWebpage(id: GalleryId, page: GalleryPage): Url =
        URLBuilder(baseUrl())
            .apply {
                path(G_PATH_SEGMENT, "${id.value}", "${page.index + 1}/")
            }
            .build()

    val Url.lastSegmentFileExtension: String?
        get() = segments
            .lastOrNull()
            ?.substringAfter('.')
            ?.ifEmpty { null }

    fun Url.isGalleryDetailUrl(): GalleryId? {
        // https://nhentai.net/g/[galleryId]/
        if ((protocol == URLProtocol.HTTP || protocol == URLProtocol.HTTPS) && host == HOST &&
            segments.first() == G_PATH_SEGMENT
        ) {
            return segments
                .getOrNull(1)
                ?.toLongOrNull()
                ?.let { GalleryId(it) }
        }

        return null
    }

    fun galleryWebPage(id: GalleryId, commentsSection: Boolean = false): Url =
        URLBuilder(baseUrl())
            .apply {
                path(G_PATH_SEGMENT, "${id.value}")
                if (commentsSection) appendEncodedPathSegments("#comment-post-container")
            }
            .build()
}
