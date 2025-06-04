package com.github.damianjester.nclient.ui

import android.content.Intent
import android.net.Uri
import com.github.damianjester.nclient.net.NHentaiUrl.isGalleryDetailUrl
import com.github.damianjester.nclient.net.NHentaiUrl.isGalleryPageUrl
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import io.ktor.http.Url

interface DeepLinker {
    fun link(intent: Intent?): List<DefaultRootComponent.Config>
}

class DefaultDeepLinker(
    private val logger: Logger
) : DeepLinker {
    override fun link(intent: Intent?): List<DefaultRootComponent.Config> {
        val action: String? = intent?.action
        val data: Uri? = intent?.data

        if (action != Intent.ACTION_VIEW || data == null) {
            logger.i(
                LogTags.deeplink,
                "Intent isn't a deeplink intent (action=$action, data=$data)."
            )
            return listOf(DefaultRootComponent.Config.GallerySearch)
        }

        val url = try {
            Url(data.toString())
        } catch (ex: IllegalArgumentException) {
            logger.i(LogTags.deeplink, "Received an invalid URL in MainActivity intent.", ex)
            return listOf(DefaultRootComponent.Config.GallerySearch)
        }

        // Gallery page
        // https://nhentai.net/g/[gallery_id]/[page_number]/
        url.isGalleryPageUrl()
            ?.let { (id, page) ->
                logger.i(
                    LogTags.deeplink,
                    "Setting initial configuration to gallery pager ($id, page = $page)."
                )
                return listOf(
                    DefaultRootComponent.Config.GalleryDetails(id),
                    DefaultRootComponent.Config.GalleryPager(id, pageIndex = page - 1)
                )
            }

        // Gallery details
        // https://nhentai.net/g/[galleryId]/
        url.isGalleryDetailUrl()
            ?.let { id ->
                logger.i(
                    LogTags.deeplink,
                    "Setting initial configuration to gallery details ($id)."
                )
                return listOf(DefaultRootComponent.Config.GalleryDetails(id))
            }

        logger.i(
            LogTags.deeplink,
            "Unsure how to map intent data to an initial configuration (action=$action, data=$data)."
        )
        return listOf(DefaultRootComponent.Config.GallerySearch)
    }
}
