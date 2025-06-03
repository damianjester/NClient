package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.core.GalleryNotFound
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.net.models.Comment
import com.github.damianjester.nclient.net.models.CommentsResponse
import com.github.damianjester.nclient.net.models.GalleryDetailsResponse
import com.github.damianjester.nclient.net.models.GallerySummariesResponse
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
import org.jsoup.Jsoup

interface NHentaiHttpClient {
    suspend fun getGallerySummaries(page: Int): GallerySummariesResponse

    suspend fun getGalleryDetails(id: GalleryId): Result<GalleryDetailsResponse, GalleryNotFound>

    suspend fun getComments(id: GalleryId): CommentsResponse
}

class ScrapperNHentaiHttpClient(
    private val client: HttpClient,
    private val logger: Logger,
    private val dispatchers: NClientDispatchers,
    private val scrapper: NHentaiScrapper
) : NHentaiHttpClient {
    override suspend fun getGallerySummaries(page: Int): GallerySummariesResponse = withContext(dispatchers.IO) {
        // https://nhentai.net/?page={page}
        val response = client.catchingGet(NHentaiUrl.galleriesWebpage(page))

        val document = response
            .bodyAsChannel()
            .toInputStream()
            .let { Jsoup.parse(it, "UTF-8", NHentaiUrl.baseUrl().toString()) }

        val galleries = scrapper.scrapeGallerySummaries(document)

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

        Result.Ok(
            GalleryDetailsResponse(
                gallery = scrapper.scrapeGalleryDetails(document),
                coverUrl = scrapper.scrapeGalleryDetailsCover(document),
                related = scrapper.scrapeRelatedGalleries(document),
            )
        )
    }

    override suspend fun getComments(id: GalleryId): CommentsResponse = withContext(dispatchers.IO) {
        client.catchingGet(NHentaiUrl.comments(id))
            .catchingBody<List<Comment>>()
            .let { CommentsResponse(it) }
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
