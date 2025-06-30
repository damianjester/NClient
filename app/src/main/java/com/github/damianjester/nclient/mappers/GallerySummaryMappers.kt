package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GallerySummary

fun mapRowToGallerySummary(
    id: Long,
    mediaId: Long,
    prettyTitle: String,
    coverThumbnailFileExtension: String?,
    isEnglish: Long?,
    isJapanese: Long?,
    isChinese: Long?,
): GallerySummary = GallerySummary(
    id = GalleryId(id),
    title = prettyTitle,
    language = mapToGalleryLanguage(isEnglish, isJapanese, isChinese),
    images = mapToRemoteGalleryImage(mediaId, coverThumbnailFileExtension)
)
