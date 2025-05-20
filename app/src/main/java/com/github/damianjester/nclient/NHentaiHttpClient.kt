package com.github.damianjester.nclient

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.utils.io.jvm.javaio.toInputStream
import org.jsoup.Jsoup

data class ListGallery(
    val id: GalleryId,
    val title: String,
    val mediaId: Long,
    val coverThumbnailUrl: Url,
    val tagIds: List<GalleryTagId>, // Also contains language, see SpecialTagIds
)

data class DetailsGallery(
    val id: GalleryId,
    val title: String,
)

@JvmInline
value class GalleryId(val id: Long)

@JvmInline
value class GalleryTagId(val id: Long)

data class GalleriesResponse(
    val galleries: List<ListGallery>,
)

data class GalleryResponse(
    val gallery: DetailsGallery
)

interface NHentaiHttpClient {
    suspend fun getGalleries(page: Int): GalleriesResponse
    suspend fun getGallery(id: GalleryId): GalleryResponse
}

class ScrapperNHentaiHttpClient(
    private val client: HttpClient,
) : NHentaiHttpClient {

    override suspend fun getGalleries(page: Int): GalleriesResponse {

        // https://nhentai.net/?page={page}
        val response = client.get("https://nhentai.net/?page=$page")
        val bodyInputStream = response.bodyAsChannel().toInputStream()

        val document = Jsoup.parse(bodyInputStream, "UTF-8", "https://nhentai.net/")
        val gallery = document.getElementsByClass("gallery")

        val galleries: List<ListGallery> = buildList {
            for (element in gallery) {

                val anchor = requireNotNull(element.getElementsByTag("a").first())
                val image = requireNotNull(element.getElementsByTag("img").first())

                val id = GalleryId(anchor.attr("href").substring(3, anchor.attr("href").length - 1).toLong())
                val title = element.getElementsByTag("div").first()!!.text()
                val srcUrl =
                    Url(if (image.hasAttr("data-src")) image.attr("data-src") else image.attr("src"))
                val mediaId = srcUrl.segments[srcUrl.segments.indexOf("galleries") + 1]
                    .toLong()
                val tagIds = element.attr("data-tags").split(" ")
                    .map { GalleryTagId(it.toLong()) }

                add(
                    ListGallery(
                        id = id,
                        title = title,
                        mediaId = mediaId,
                        coverThumbnailUrl = srcUrl,
                        tagIds = tagIds
                    )
                )
            }
        }

        return GalleriesResponse(galleries)
    }

    override suspend fun getGallery(id: GalleryId): GalleryResponse {

        // https://nhentai.net/g/{gallery_id}
        val response = client.get("https://nhentai.net/g/${id.id}")

        TODO()
    }

}
