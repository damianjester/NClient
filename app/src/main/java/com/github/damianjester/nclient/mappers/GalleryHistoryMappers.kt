package com.github.damianjester.nclient.mappers

import com.github.damianjester.nclient.SelectHistoryVisits
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.core.models.GalleryImageFileType
import com.github.damianjester.nclient.core.models.GalleryLanguage
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.GallerySummaryImages
import com.github.damianjester.nclient.core.models.GalleryVisit
import com.github.damianjester.nclient.core.models.MediaId
import com.github.damianjester.nclient.db.models.GalleryHistoryWithTagIds
import com.github.damianjester.nclient.net.NHentaiUrl
import kotlinx.datetime.Instant

fun List<SelectHistoryVisits>.toGalleryHistoryWithTagIds(): List<GalleryHistoryWithTagIds> =
    groupBy { it.id }
        .map<Long, List<SelectHistoryVisits>, GalleryHistoryWithTagIds> { (id, rows) ->
            val first = rows.first()
            val tagIds = rows.map { it.tagId }
            GalleryHistoryWithTagIds(
                id = id,
                title = first.prettyTitle,
                mediaId = first.mediaId,
                coverThumbnailFileExtension = first.coverThumbnailFileExtension,
                tagIds = tagIds,
                lastVisitInstant = first.lastVisitInstant,
                visitCount = first.visitCount
            )
        }

fun GalleryHistoryWithTagIds.toGalleryVisit(): GalleryVisit {
    return GalleryVisit(
        gallery = GallerySummary(
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
        ),
        lastVisit = Instant.fromEpochSeconds(lastVisitInstant),
        visitCount = visitCount.toInt()
    )
}
