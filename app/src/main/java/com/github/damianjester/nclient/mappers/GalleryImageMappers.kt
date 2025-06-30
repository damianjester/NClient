package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.core.models.GalleryImageFileType
import com.github.damianjester.nclient.core.models.GallerySummaryImages
import com.github.damianjester.nclient.core.models.MediaId
import com.github.damianjester.nclient.net.NHentaiUrl

fun mapToRemoteGalleryImage(
    mediaId: Long,
    coverThumbnailFileExtension: String?,
): GallerySummaryImages.Remote = GallerySummaryImages.Remote(
    thumbnail = GalleryImage.Remote(
        url = NHentaiUrl.galleryCoverThumbnail(
            mediaId = MediaId(mediaId),
            fileType = coverThumbnailFileExtension
                ?.let { GalleryImageFileType.fromFileExtension(it) }
                ?: GalleryImageFileType.WEBP(hasWebpExtension = false)
        )
    )
)
