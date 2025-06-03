package com.github.damianjester.nclient.db.mappers

import com.github.damianjester.nclient.GalleryDetailsEntity
import com.github.damianjester.nclient.GalleryPageEntity
import com.github.damianjester.nclient.GallerySummaryEntity
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
import com.github.damianjester.nclient.net.GalleryResponse
import com.github.damianjester.nclient.net.NHentaiUrl
import com.github.damianjester.nclient.net.NHentaiUrl.lastSegmentFileExtension
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun GalleryResponse.Success.toGallerySummary() =
    GallerySummaryEntity(
        id = gallery.id.value,
        mediaId = gallery.mediaId,
        prettyTitle = gallery.title.pretty,
        coverThumbnailFileExtension = coverUrl?.lastSegmentFileExtension
            ?: GalleryImageFileType
                .fromType(gallery.images.thumbnail.t)
                .toFileExtension()
    )

fun GalleryResponse.Success.toGalleryDetailsEntity(): GalleryDetailsEntity {
    val now = Clock.System.now()
    return GalleryDetailsEntity(
        galleryId = gallery.id.value,
        coverFileExtension = coverUrl?.lastSegmentFileExtension
            ?: GalleryImageFileType
                .fromType(gallery.images.cover.t)
                .toFileExtension(),
        numFavorites = gallery.numFavorites.toLong(),
        englishTitle = gallery.title.english,
        japaneseTitle = gallery.title.japanese,
        uploadDate = gallery.uploadDate,
        createdAt = now.epochSeconds,
        updatedAt = now.epochSeconds
    )
}

fun GalleryResponse.Success.toGalleryDetailsPages() =
    gallery.images.pages
        .mapIndexed { i, p ->
            GalleryPageEntity(
                galleryId = gallery.id.value,
                pageIndex = i.toLong(),
                fileExtension = GalleryImageFileType.fromType(p.t).toFileExtension(),
                width = p.w.toLong(),
                height = p.h.toLong()
            )
        }

fun GalleryResponse.Success.toRelatedGalleries() =
    related.map { gal ->

        val gallery = GallerySummaryEntity(
            id = gal.id.value,
            prettyTitle = gal.title,
            mediaId = gal.mediaId,
            coverThumbnailFileExtension = gal.coverThumbnailUrl.lastSegmentFileExtension,
        )

        gallery to gal.tagIds
    }

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
