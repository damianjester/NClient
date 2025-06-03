package com.github.damianjester.nclient.db.mappers

import com.github.damianjester.nclient.SelectRelatedForGallery
import com.github.damianjester.nclient.SelectSummaryWithDetails
import com.github.damianjester.nclient.core.models.Gallery
import com.github.damianjester.nclient.core.models.GalleryCover
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.core.models.GalleryImageFileType
import com.github.damianjester.nclient.core.models.GalleryLanguage
import com.github.damianjester.nclient.core.models.GalleryPage
import com.github.damianjester.nclient.core.models.GalleryPageImages
import com.github.damianjester.nclient.core.models.GalleryTitle
import com.github.damianjester.nclient.core.models.MediaId
import com.github.damianjester.nclient.core.models.RelatedGallery
import com.github.damianjester.nclient.core.models.Resolution
import com.github.damianjester.nclient.db.GalleryPageWithMediaId
import com.github.damianjester.nclient.db.GalleryWithTagIds
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun List<SelectRelatedForGallery>.toGalleriesWithTagIds() =
    groupBy { it.id }
        .map { (id, rows) ->
            val first = rows.first()
            val tagIds = rows.map { it.tagId }
            GalleryWithTagIds(
                id = id,
                title = first.prettyTitle,
                mediaId = first.mediaId,
                coverThumbnailFileExtension = first.coverThumbnailFileExtension,
                tagIds = tagIds
            )
        }

fun GalleryWithTagIds.toRelatedGallery() =
    RelatedGallery(
        id = GalleryId(id),
        title = title,
        language = GalleryLanguage.fromLongTagId(tagIds),
        image = GalleryImage.Remote(
            NHentaiUrl.galleryCoverThumbnail(
                MediaId(mediaId),
                coverThumbnailFileExtension
                    ?.let { GalleryImageFileType.fromFileExtension(it) }
                    ?: GalleryImageFileType.WEBP(hasWebpExtension = false) // Cover thumbnail image file type unknown, make a best guess
            )
        )
    )

fun SelectSummaryWithDetails.toGallery() =
    Gallery(
        id = GalleryId(id),
        title = GalleryTitle(
            pretty = prettyTitle,
            english = englishTitle,
            japanese = japaneseTitle
        ),
        cover = GalleryCover(
            thumbnailUrl = NHentaiUrl.galleryCoverThumbnail(
                mediaId = MediaId(mediaId),
                fileType = coverThumbnailFileExtension
                    ?.let { GalleryImageFileType.fromFileExtension(it) }
                    ?: GalleryImageFileType.WEBP(hasWebpExtension = false) // Cover thumbnail image file type unknown, make a best guess
            ),
            originalUrl = NHentaiUrl.galleryCover(
                mediaId = MediaId(mediaId),
                fileType = coverFileExtension
                    ?.let { GalleryImageFileType.fromFileExtension(it) }
                    ?: GalleryImageFileType.WEBP(hasWebpExtension = false) // Cover image file type unknown, make a best guess
            )
        ),
        updated = Instant.fromEpochSeconds(uploadDate).toLocalDateTime(TimeZone.UTC),
        favoriteCount = numFavorites.toInt()
    )

fun GalleryPageWithMediaId.toGalleryPage(): GalleryPage {
    val fileType = GalleryImageFileType.fromFileExtension(fileExtension)

    return GalleryPage(
        index = pageIndex.toInt(),
        image = GalleryPageImages.Remote(
            remoteThumbnail = GalleryImage.Remote(
                NHentaiUrl.galleryPageThumbnail(
                    mediaId = MediaId(mediaId),
                    pageNumber = pageIndex.toInt() + 1,
                    fileType = fileType
                )
            ),
            remoteOriginal = GalleryImage.Remote(
                NHentaiUrl.galleryPage(
                    mediaId = MediaId(mediaId),
                    pageNumber = pageIndex.toInt() + 1,
                    fileType = fileType
                )
            ),
        ),
        resolution = Resolution(width.toInt(), height.toInt())
    )
}
