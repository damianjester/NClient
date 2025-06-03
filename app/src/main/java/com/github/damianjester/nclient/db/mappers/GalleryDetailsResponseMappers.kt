package com.github.damianjester.nclient.db.mappers

import com.github.damianjester.nclient.GalleryDetailsEntity
import com.github.damianjester.nclient.GalleryPageEntity
import com.github.damianjester.nclient.GallerySummaryEntity
import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.core.models.GalleryImageFileType
import com.github.damianjester.nclient.net.NHentaiUrl.lastSegmentFileExtension
import com.github.damianjester.nclient.net.models.GalleryDetailsResponse
import kotlinx.datetime.Clock

fun GalleryDetailsResponse.toGallerySummary() =
    GallerySummaryEntity(
        id = gallery.id.value,
        mediaId = gallery.mediaId,
        prettyTitle = gallery.title.pretty,
        coverThumbnailFileExtension = coverUrl?.lastSegmentFileExtension
            ?: GalleryImageFileType
                .fromType(gallery.images.thumbnail.t)
                .toFileExtension()
    )

fun GalleryDetailsResponse.toGalleryDetailsEntity(): GalleryDetailsEntity {
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

fun GalleryDetailsResponse.toGalleryDetailsPages() =
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

fun GalleryDetailsResponse.toGalleryDetailsTags() =
    gallery.tags
        .map { t ->
            TagEntity(
                id = t.id.value,
                type = t.type,
                name = t.name,
                count = t.count.toLong(),
                urlPath = t.url
            )
        }

fun GalleryDetailsResponse.toRelatedGalleries() =
    related.map { gal ->

        val gallery = GallerySummaryEntity(
            id = gal.id.value,
            prettyTitle = gal.title,
            mediaId = gal.mediaId,
            coverThumbnailFileExtension = gal.coverThumbnailUrl.lastSegmentFileExtension,
        )

        gallery to gal.tagIds
    }

