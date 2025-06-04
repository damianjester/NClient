package com.github.damianjester.nclient.ui

import android.content.Intent
import android.net.Uri
import com.github.damianjester.nclient.net.NHentaiUrl.isGalleryDetailUrl
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import io.ktor.http.Url

interface DeepLinker {
    fun link(intent: Intent?): DefaultRootComponent.Config
}

class DefaultDeepLinker(
    private val logger: Logger
) : DeepLinker {
    override fun link(intent: Intent?): DefaultRootComponent.Config {
        val action: String? = intent?.action
        val data: Uri? = intent?.data

        if (action != Intent.ACTION_VIEW || data == null) {
            logger.i(
                LogTags.deeplink,
                "Intent isn't a deeplink intent (action=$action, data=$data)."
            )
            return DefaultRootComponent.Config.GallerySearch
        }

        val url = try {
            Url(data.toString())
        } catch (ex: IllegalArgumentException) {
            logger.i(LogTags.deeplink, "Received an invalid URL in MainActivity intent.", ex)
            return DefaultRootComponent.Config.GallerySearch
        }

        // Gallery details
        // https://nhentai.net/g/[galleryId]/
        val galleryId = url.isGalleryDetailUrl()
        if (galleryId != null) {
            logger.i(
                LogTags.deeplink,
                "Setting initial configuration to gallery details ($galleryId)."
            )
            return DefaultRootComponent.Config.GalleryDetails(galleryId)
        }

        logger.i(
            LogTags.deeplink,
            "Unsure how to map intent data to an initial configuration (action=$action, data=$data)."
        )
        return DefaultRootComponent.Config.GallerySearch
    }
}
