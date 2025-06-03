package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.core.GalleryNotFound
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryTagId
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.net.models.Comment
import com.github.damianjester.nclient.net.models.CommentsResponse
import com.github.damianjester.nclient.net.models.GalleryDetails
import com.github.damianjester.nclient.net.models.GalleryDetailsResponse
import com.github.damianjester.nclient.net.models.GallerySummariesResponse
import com.github.damianjester.nclient.net.models.GallerySummary
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

interface NHentaiHttpClient {
    suspend fun getGallerySummaries(page: Int): GallerySummariesResponse

    suspend fun getGalleryDetails(id: GalleryId): Result<GalleryDetailsResponse, GalleryNotFound>

    suspend fun getComments(id: GalleryId): CommentsResponse
}

class ScrapperNHentaiHttpClient(
    private val client: HttpClient,
    private val logger: Logger,
    private val dispatchers: NClientDispatchers,
) : NHentaiHttpClient {
    override suspend fun getGallerySummaries(page: Int): GallerySummariesResponse = withContext(dispatchers.IO) {
        // https://nhentai.net/?page={page}
        val response = client.catchingGet(NHentaiUrl.galleriesWebpage(page))

        val document = response
            .bodyAsChannel()
            .toInputStream()
            .let { Jsoup.parse(it, "UTF-8", NHentaiUrl.baseUrl().toString()) }

        val galleries = document
            .getElementsByClass("gallery")
            .map { element -> scrapeListGallery(element) }

        GallerySummariesResponse(galleries)
    }

    override suspend fun getGalleryDetails(id: GalleryId): Result<GalleryDetailsResponse, GalleryNotFound> = withContext(dispatchers.IO) {
        // https://nhentai.net/g/{gallery_id}
        val response = client.catchingGet(NHentaiUrl.galleryWebpage(id))

        if (response.status == HttpStatusCode.NotFound) {
            return@withContext Result.Err(GalleryNotFound(id))
        }

        val document = response
            .bodyAsChannel()
            .toInputStream()
            .let { Jsoup.parse(it, "UTF-8", NHentaiUrl.baseUrl().toString()) }

        val script = document.getElementsByTag("script").lastOrNull()
            ?: scrapeError("Failed to find last script tag in document.")

        val galleryJson = StringEscapeUtils.unescapeJava(substringGalleryJson(script.html()))
        val galleryDetails = try {
            Json.Default.decodeFromString<GalleryDetails>(galleryJson)
        } catch (ex: SerializationException) {
            val ex = NHentaiClientSerializationException(ex)
            logger.e(LogTags.http, "Failed to decode gallery details JSON.", ex)
            throw ex
        }

        val coverImage = document
            .getElementById("cover")
            ?.getElementsByTag("img")
            ?.firstOrNull()

        val coverUrl = coverImage
            ?.attr("data-src")
            ?.let { Url(it) }
            .also {
                if (it == null) {
                    logger.w(
                        LogTags.http,
                        "Unable to scrape gallery summary cover URL. img: ${coverImage?.html()}"
                    )
                }
            }

        val related = document
            .getElementById("related-container")
            ?.getElementsByClass("gallery")
            ?.let { gal -> gal.map { element -> scrapeListGallery(element) } }
            ?: emptyList()

        Result.Ok(
            GalleryDetailsResponse(
                gallery = galleryDetails,
                coverUrl = coverUrl,
                related = related,
            )
        )
    }

    override suspend fun getComments(id: GalleryId): CommentsResponse = withContext(dispatchers.IO) {
        client.catchingGet(NHentaiUrl.comments(id))
            .catchingBody<List<Comment>>()
            .let { CommentsResponse(it) }
    }

    private fun scrapeListGallery(element: Element): GallerySummary {
        val anchor = element.getElementsByTag("a").firstOrNull()
            ?: scrapeError("Unable to find first anchor element.")
        val image = element.getElementsByTag("img").firstOrNull()
            ?: scrapeError("Unable to find first image element.")

        val id = anchor.attr("href")
            .substring(3, anchor.attr("href").length - 1)
            .toLongOrNull()
            ?.let { GalleryId(it) }
            ?: scrapeError("Failed to extract gallery id for gallery summary.")

        val firstDiv = element.getElementsByTag("div").firstOrNull()
        val title = firstDiv
            ?.text()
            ?.ifEmpty { scrapeError("Gallery title was empty. div element: ${firstDiv.html()}") }
            ?: scrapeError("Gallery title was null.")

        val srcUrl = (if (image.hasAttr("data-src")) image.attr("data-src") else image.attr("src"))
            .ifEmpty { scrapeError("Thumbnail URL was empty. img element: ${image.html()}.") }
            .let { Url(it) }

        val mediaId = srcUrl.segments
            .getOrNull(srcUrl.segments.indexOf("galleries") + 1)
            ?.toLongOrNull()
            ?: scrapeError("Failed to scrape media ID. img src: $srcUrl.")

        val tagIds = element.attr("data-tags")
            .split(" ")
            .map {
                val id = it.toLongOrNull()
                    ?: scrapeError("Unable to parse tag ID. id = $it.")
                GalleryTagId(id)
            }

        return GallerySummary(
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

    /**
     * Transforms a subset of caught exceptions to sub types of [NHentaiClientException] and
     * rethrows them. This makes it easier to catch them downstream and handle them appropriately.
     */
    private suspend inline fun HttpClient.catchingGet(
        url: Url,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse {
        return try {
            client.get(url)
        } catch (ex: java.net.UnknownHostException) {
            val ex = NHentaiClientConnectionException(ex)
            logger.e(LogTags.http, "Host unknown when making GET a request to $url.", ex)
            throw ex
        } catch (ex: io.ktor.client.plugins.HttpRequestTimeoutException) {
            val ex = NHentaiClientConnectionException(ex)
            logger.e(LogTags.http, "Timeout when making GET a request to $url.", ex)
            throw ex
        } catch (ex: io.ktor.utils.io.ClosedByteChannelException) {
            val ex = NHentaiClientConnectionException(ex)
            logger.e(LogTags.http, "Timeout when making GET a request to $url.", ex)
            throw ex
        }
    }

    private suspend inline fun <reified T> HttpResponse.catchingBody(): T {
        return try {
            call.bodyNullable(typeInfo<T>()) as T
        } catch (ex: SerializationException) {
            val ex = NHentaiClientSerializationException(ex)
            logger.e(LogTags.http, "Failed to decode body.", ex)
            throw ex
        }
    }
}
