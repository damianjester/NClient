package com.github.damianjester.nclient.db.mappers

import com.github.damianjester.nclient.GalleryHasTag
import com.github.damianjester.nclient.GallerySummaryEntity
import com.github.damianjester.nclient.net.NHentaiUrl.lastSegmentFileExtension
import com.github.damianjester.nclient.net.models.GallerySummariesResponse
import com.github.damianjester.nclient.net.models.GallerySummary

fun GallerySummariesResponse.toGallerySummaryEntityWithHasTags(): List<Pair<GallerySummaryEntity, List<GalleryHasTag>>> =
    galleries.map { gal ->
        val gallery = gal.toGallerySummaryEntity()
        val hasTags = gal.tagIds.map { tagId -> GalleryHasTag(gal.id.value, tagId.value) }
        gallery to hasTags
    }

fun GallerySummary.toGallerySummaryEntity() =
    GallerySummaryEntity(
        id = id.value,
        prettyTitle = title,
        mediaId = mediaId,
        coverThumbnailFileExtension = coverThumbnailUrl.lastSegmentFileExtension,
    )

