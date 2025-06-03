package com.github.damianjester.nclient.db.mappers

import com.github.damianjester.nclient.GalleryHasTag
import com.github.damianjester.nclient.GallerySummaryEntity
import com.github.damianjester.nclient.SelectSummariesFoQuery
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryImage
import com.github.damianjester.nclient.core.GalleryImageFileType
import com.github.damianjester.nclient.core.GalleryLanguage
import com.github.damianjester.nclient.core.GallerySearchItem
import com.github.damianjester.nclient.core.GallerySearchItemImages
import com.github.damianjester.nclient.core.MediaId
import com.github.damianjester.nclient.db.GalleryWithTagIds
import com.github.damianjester.nclient.net.GalleriesResponse
import com.github.damianjester.nclient.net.ListGallery
import com.github.damianjester.nclient.net.NHentaiUrl
import com.github.damianjester.nclient.net.NHentaiUrl.lastSegmentFileExtension

fun GalleriesResponse.toGallerySummaryEntityWithHasTags(): List<Pair<GallerySummaryEntity, List<GalleryHasTag>>> =
    galleries.map { gal ->
        val gallery = gal.toGallerySummaryEntity()
        val hasTags = gal.tagIds.map { tagId -> GalleryHasTag(gal.id.value, tagId.value) }
        gallery to hasTags
    }

fun ListGallery.toGallerySummaryEntity() =
    GallerySummaryEntity(
        id = id.value,
        prettyTitle = title,
        mediaId = mediaId,
        coverThumbnailFileExtension = coverThumbnailUrl.lastSegmentFileExtension,
    )

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

fun GalleryWithTagIds.toGallerySearchItem() =
    GallerySearchItem(
        id = GalleryId(id),
        title = title,
        language = GalleryLanguage.fromLongTagId(tagIds),
        images = GallerySearchItemImages.Remote(
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
