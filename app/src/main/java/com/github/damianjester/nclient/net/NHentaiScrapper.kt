package com.github.damianjester.nclient.net

import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryTagId
import com.github.damianjester.nclient.net.models.GalleryDetails
import com.github.damianjester.nclient.net.models.GallerySummary
import io.ktor.http.Url
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

interface NHentaiScrapper {
    fun scrapeGallerySummaries(document: Document): List<GallerySummary>

    fun scrapeGalleryDetails(document: Document): GalleryDetails

    fun scrapeGalleryDetailsCover(document: Document): Url?

    fun scrapeRelatedGalleries(document: Document): List<GallerySummary>
}

class DefaultNHentaiScrapper : NHentaiScrapper {
    override fun scrapeGallerySummaries(document: Document): List<GallerySummary> {
        return document
            .getElementsByClass("gallery")
            .map { element -> scrapeListGallery(element) }
    }

    override fun scrapeGalleryDetails(document: Document): GalleryDetails {
        val script = document.getElementsByTag("script").lastOrNull()
            ?: scrapeError("Failed to find last script tag in document.")

        val galleryJson = StringEscapeUtils.unescapeJava(substringGalleryJson(script.html()))
        val galleryDetails = try {
            Json.Default.decodeFromString<GalleryDetails>(galleryJson)
        } catch (ex: SerializationException) {
            throw NHentaiClientSerializationException(ex)
        }

        return galleryDetails
    }

    override fun scrapeGalleryDetailsCover(document: Document): Url? {
        val coverImage = document
            .getElementById("cover")
            ?.getElementsByTag("img")
            ?.firstOrNull()

        return coverImage?.attr("data-src")?.let { Url(it) }
    }

    override fun scrapeRelatedGalleries(document: Document): List<GallerySummary> {
        return document
            .getElementById("related-container")
            ?.getElementsByClass("gallery")
            ?.let { gal -> gal.map { element -> scrapeListGallery(element) } }
            ?: emptyList()
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
}
