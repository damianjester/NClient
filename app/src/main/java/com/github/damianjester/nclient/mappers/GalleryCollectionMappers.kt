package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.ViewCollections
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.core.models.GalleryImageFileType
import com.github.damianjester.nclient.core.models.MediaId
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.datetime.Instant

fun List<ViewCollections>.toGalleryCollections() =
    groupBy { it.id }
        .map { (collectionId, rows) ->
            val firstRow = rows.first()
            GalleryCollection(
                id = GalleryCollectionId(collectionId),
                name = firstRow.name,
                lastUpdated = Instant.fromEpochSeconds(firstRow.updateAtInstant),
                thumbnails = rows.toThumbnails(),
                size = firstRow.collectionSize?.toInt() ?: 0
            )
        }

private fun List<ViewCollections>.toThumbnails(): List<GalleryImage.Remote> =
    mapNotNull { row ->
        if (row.mediaId != null) {
            row.mediaId to row.coverThumbnailFileExtension
        } else {
            null
        }
    }.map { (mediaId, thumbExt) ->
        GalleryImage.Remote(
            NHentaiUrl.galleryCoverThumbnail(
                mediaId = MediaId(mediaId),
                fileType = thumbExt
                    ?.let { GalleryImageFileType.fromFileExtension(it) }
                    ?: GalleryImageFileType.WEBP(hasWebpExtension = false)
            )
        )
    }
