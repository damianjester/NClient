package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryTagId
import com.github.damianjester.nclient.utils.NClientDispatchers
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

interface NHentaiHttpClient {
    suspend fun getGalleries(page: Int): GalleriesResponse

    suspend fun getGallery(id: GalleryId): GalleryResponse

    suspend fun getComments(id: GalleryId): List<CommentResponse>
}

class ScrapperNHentaiHttpClient(
    private val client: HttpClient,
    private val dispatchers: NClientDispatchers,
) : NHentaiHttpClient {
    override suspend fun getGalleries(page: Int): GalleriesResponse = withContext(dispatchers.IO) {
        // https://nhentai.net/?page={page}
        val response = client.get("https://nhentai.net/?page=$page")
        val bodyInputStream = response.bodyAsChannel().toInputStream()

        val document = Jsoup.parse(bodyInputStream, "UTF-8", "https://nhentai.net/")
        val gallery = document.getElementsByClass("gallery")

        val galleries = gallery.map { element -> scrapeListGallery(element) }

        GalleriesResponse(galleries)
    }

    override suspend fun getGallery(id: GalleryId): GalleryResponse = withContext(dispatchers.IO) {
        // https://nhentai.net/g/{gallery_id}
        val response = client.get("https://nhentai.net/g/${id.value}/")
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

        GalleryResponse(
            gallery = detailsGallery,
            related = related,
            isUserFavorite = isFavorite
        )
    }

    override suspend fun getComments(id: GalleryId): List<CommentResponse> {
        return client.get(NHentaiUrl.commentsUrl(id.value))
            .body<List<CommentResponse>>()
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
