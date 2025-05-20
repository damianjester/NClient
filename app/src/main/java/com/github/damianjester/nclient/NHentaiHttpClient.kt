package com.github.damianjester.nclient

import com.github.damianjester.nclient.api.InspectorV3.InvalidResponseException
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

data class ListGallery(
    val id: GalleryId,
    val title: String,
    val mediaId: Long,
    val coverThumbnailUrl: Url,
    val tagIds: List<GalleryTagId>, // Also contains language, see SpecialTagIds
)

@Serializable
data class DetailsGallery(
    val id: GalleryId,
    @SerialName("media_id") val mediaId: Long,
    val title: GalleryTitle,
    val images: GalleryImages,
    val scanlator: String,
    @SerialName("upload_date") val uploadDate: Long,
    val tags: List<GalleryTag>,
    @SerialName("num_pages") val numPages: Int,
    @SerialName("num_favorites") val numFavorites: Int,
)

@Serializable
data class GalleryTitle(
    val english: String,
    val japanese: String,
    val pretty: String,
)

@Serializable
data class GalleryImages(
    val pages: List<GalleryImage>,
    val cover: GalleryImage,
    val thumbnail: GalleryImage,
)

@Serializable
data class GalleryImage(
    val t: String, // Either "w" or ???
    val w: Int,
    val h: Int,
)

@JvmInline
@Serializable
value class GalleryId(val id: Long)

@JvmInline
@Serializable
value class GalleryTagId(val id: Long)

@Serializable
data class GalleryTag(
    val id: GalleryTagId,
    val type: String, // "tag", "language", "category", "parody", "character"
    val name: String,
    val url: String, // Actually path, e.g. /tag/big-breasts/
    val count: Int,
)

data class GalleriesResponse(
    val galleries: List<ListGallery>,
)

data class GalleryResponse(
    val gallery: DetailsGallery,
    val related: List<ListGallery>,
    val isUserFavorite: Boolean,
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

        val galleries = gallery.map { element -> scrapeListGallery(element) }

        return GalleriesResponse(galleries)
    }

    override suspend fun getGallery(id: GalleryId): GalleryResponse {

        // https://nhentai.net/g/{gallery_id}
        val response = client.get("https://nhentai.net/g/${id.id}")
        val bodyInputStream = response.bodyAsChannel().toInputStream()

        val document = Jsoup.parse(bodyInputStream, "UTF-8", "https://nhentai.net/")
        val script = requireNotNull(document.getElementsByTag("script").last())

        val secondScriptHtml = script.html()
        val json: String = StringEscapeUtils.unescapeJava(substringGalleryJson(secondScriptHtml))
        val detailsGallery = Json.Default.decodeFromString<DetailsGallery>(json)

        val relatedContainer = document.getElementById("related-container")
        val related = if (relatedContainer != null) {
            val gallery = relatedContainer.getElementsByClass("gallery")
            gallery.map { element -> scrapeListGallery(element) }
        } else {
            emptyList()
        }

        val isFavorite = try {
            document.getElementById("favorite")?.getElementsByTag("span")?.get(0)
                ?.text() == "Unfavorite"
        } catch (e: Exception) {
            false
        }

        return GalleryResponse(
            gallery = detailsGallery,
            related = related,
            isUserFavorite = isFavorite
        )
    }

    private fun scrapeListGallery(element: Element): ListGallery {

        val anchor = requireNotNull(element.getElementsByTag("a").first())
        val image = requireNotNull(element.getElementsByTag("img").first())

        val id = GalleryId(
            anchor.attr("href").substring(3, anchor.attr("href").length - 1).toLong()
        )
        val title = element.getElementsByTag("div").first()!!.text()
        val srcUrl =
            Url(if (image.hasAttr("data-src")) image.attr("data-src") else image.attr("src"))
        val mediaId = srcUrl.segments[srcUrl.segments.indexOf("galleries") + 1]
            .toLong()
        val tagIds = element.attr("data-tags").split(" ")
            .map { GalleryTagId(it.toLong()) }

        return ListGallery(
            id = id,
            title = title,
            mediaId = mediaId,
            coverThumbnailUrl = srcUrl,
            tagIds = tagIds
        )
    }

    private fun substringGalleryJson(scriptHtml: String): String {

        val jsParseStart = """JSON.parse(""""
        val jsonParseStartIndex = scriptHtml.indexOf(jsParseStart) + jsParseStart.length
        val jsonParseEndIndex = scriptHtml.lastIndexOf(");") - 1
        val json = scriptHtml.substring(jsonParseStartIndex, jsonParseEndIndex)

        return json
    }

}
