package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.core.models.GalleryLanguage

fun mapToGalleryLanguage(
    isEnglish: Long?,
    isJapanese: Long?,
    isChinese: Long?,
) = when {
    isEnglish == 1L -> GalleryLanguage.English
    isJapanese == 1L -> GalleryLanguage.Japanese
    isChinese == 1L -> GalleryLanguage.Chinese
    else -> GalleryLanguage.Unknown
}
