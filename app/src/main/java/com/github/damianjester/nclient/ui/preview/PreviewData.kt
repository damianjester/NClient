package com.github.damianjester.nclient.ui.preview

import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryImage
import com.github.damianjester.nclient.core.models.GalleryLanguage
import com.github.damianjester.nclient.core.models.GallerySummary
import com.github.damianjester.nclient.core.models.GallerySummaryImages
import io.ktor.http.Url
import kotlinx.datetime.Instant

object PreviewData {
    val summaries
        get() = listOf(
            GallerySummary(
                id = GalleryId(1),
                title = "Ice Cream Memories",
                language = GalleryLanguage.English,
                images = GallerySummaryImages.Remote(
                    thumbnail = GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/1/thumb.jpg")),
                ),
            ),
            GallerySummary(
                id = GalleryId(2),
                title = "Wild Ride",
                language = GalleryLanguage.Japanese,
                images = GallerySummaryImages.Remote(
                    thumbnail = GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/2/thumb.jpg")),
                ),
            ),
            GallerySummary(
                id = GalleryId(3),
                title = "Revenge of the Tentacles",
                language = GalleryLanguage.Chinese,
                images = GallerySummaryImages.Remote(
                    thumbnail = GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/3/thumb.jpg")),
                ),
            ),
            GallerySummary(
                id = GalleryId(4),
                title = "Digital Encounter",
                language = GalleryLanguage.Unknown,
                images = GallerySummaryImages.Remote(
                    thumbnail = GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/4/thumb.jpg")),
                ),
            )
        )

    val collections
        get() = listOf(
            GalleryCollection(
                id = GalleryCollectionId(1),
                name = "Favorites",
                lastUpdated = Instant.fromEpochSeconds(1749546345),
                thumbnails = listOf(
                    GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/3/thumb.jpg")),
                    GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/4/thumb.jpg")),
                    GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/2/thumb.jpg")),
                ),
                size = 20
            ),
            GalleryCollection(
                id = GalleryCollectionId(2),
                name = "Wholesome",
                lastUpdated = Instant.fromEpochSeconds(1749546345),
                thumbnails = listOf(
                    GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/1/thumb.jpg")),
                    GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/4/thumb.jpg")),
                ),
                size = 2
            ),
            GalleryCollection(
                id = GalleryCollectionId(3),
                name = "S-Tier",
                lastUpdated = Instant.fromEpochSeconds(1749546345),
                thumbnails = listOf(
                    GalleryImage.Remote(Url("https://t1.nhentai.net/galleries/4/thumb.jpg")),
                ),
                size = 1
            ),
            GalleryCollection(
                id = GalleryCollectionId(4),
                name = "Slop",
                lastUpdated = Instant.fromEpochSeconds(1749546345),
                thumbnails = emptyList(),
                size = 0
            )
        )
}
