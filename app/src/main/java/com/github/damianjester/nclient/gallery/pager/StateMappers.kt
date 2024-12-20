package com.github.damianjester.nclient.gallery.pager

import com.github.damianjester.nclient.api.components.Gallery
import com.github.damianjester.nclient.api.components.GenericGallery

fun GenericGallery.toModel(): GalleryPagerComponent.Gallery {

    val pages = if (isLocal) {
        toLocalPages()
    } else {
        toRemotePages()
    }

    return GalleryPagerComponent.Gallery(
        id = id.toLong(),
        title = title,
        pages = pages
    )
}

fun GenericGallery.toLocalPages(): List<GalleryPagerComponent.GalleryPage> =
    buildList {
        repeat(pageCount) { i ->
            add(
                GalleryPagerComponent.GalleryPage(
                    index = i,
                    image = GalleryPagerComponent.GalleryPageImage.Local(
                        webpageUrl = sharePageUrl(i).toString(),
                        file = galleryFolder.getPage(i + 1)
                    )
                )
            )
        }
    }

fun GenericGallery.toRemotePages(): List<GalleryPagerComponent.GalleryPage> =
    buildList {
        val gallery = this@toRemotePages as Gallery
        repeat(gallery.pageCount) { i ->
            add(
                GalleryPagerComponent.GalleryPage(
                    index = i,
                    image = GalleryPagerComponent.GalleryPageImage.Remote(
                        webpageUrl = sharePageUrl(i).toString(),
                        url = gallery.getPageUrl(i).toString()
                    )
                )
            )
        }
    }
