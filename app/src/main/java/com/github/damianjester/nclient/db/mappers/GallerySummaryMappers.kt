package com.github.damianjester.nclient.db.mappers

import com.github.damianjester.nclient.SelectSummariesFoQuery
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.core.models.GalleryImageFileType
import com.github.damianjester.nclient.core.models.GalleryLanguage
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.GallerySummaryImages
import com.github.damianjester.nclient.core.models.MediaId
import com.github.damianjester.nclient.db.models.GalleryWithTagIds
import com.github.damianjester.nclient.net.NHentaiUrl

fun List<SelectSummariesFoQuery>.toGalleriesWithTagIds() =
    groupBy { it.galleryId }
        .map { (id, rows) ->
            val first = rows.first()
            val tagIds = rows.map { it.tagId }
            GalleryWithTagIds(
                id,
                first.prettyTitle,
                first.mediaId,
                first.coverThumbnailFileExtension,
                tagIds
            )
        }

fun GalleryWithTagIds.toGallerySummary() =
    GallerySummary(
        id = GalleryId(id),
        title = title,
        language = GalleryLanguage.fromLongTagId(tagIds),
        images = GallerySummaryImages.Remote(
            thumbnail = GalleryImage.Remote(
                NHentaiUrl.galleryCoverThumbnail(
                    mediaId = MediaId(mediaId),
                    fileType = coverThumbnailFileExtension
                        ?.let { GalleryImageFileType.fromFileExtension(it) }
                        ?: GalleryImageFileType.WEBP(hasWebpExtension = false)
                )
            )
        )
    )
