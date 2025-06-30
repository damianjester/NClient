package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.GalleryVisit
import kotlinx.datetime.Instant

fun mapRowToGalleryVisit(
    id: Long,
    mediaId: Long,
    prettyTitle: String,
    coverThumbnailFileExtension: String?,
    isEnglish: Long?,
    isJapanese: Long?,
    isChinese: Long?,
    lastVisitInstant: Long,
    visitCount: Long,
) = GalleryVisit(
    gallery = GallerySummary(
        id = GalleryId(id),
        title = prettyTitle,
        language = mapToGalleryLanguage(isEnglish, isJapanese, isChinese),
        images = mapToRemoteGalleryImage(mediaId, coverThumbnailFileExtension),
    ),
    lastVisit = Instant.fromEpochSeconds(lastVisitInstant),
    visitCount = visitCount.toInt()
)
