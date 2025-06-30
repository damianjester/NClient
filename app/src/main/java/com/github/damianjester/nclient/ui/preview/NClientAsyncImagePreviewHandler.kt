@file:OptIn(ExperimentalCoilApi::class)

package com.github.damianjester.nclient.ui.preview

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import coil3.Image
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.request.ImageRequest
import com.github.damianjester.nclient.R
import org.koin.core.component.KoinComponent

class NClientAsyncImagePreviewHandler(
    private val context: Context
) : AsyncImagePreviewHandler, KoinComponent {
    val defaultPreviewImage: Image
        get() = AppCompatResources.getDrawable(context, R.drawable.ic_logo)!!.asImage()

    private fun previewImage(url: String): Image? {
        return when (url) {
            "https://t1.nhentai.net/galleries/1/thumb.jpg" ->
                AppCompatResources.getDrawable(context, R.drawable.preview_gallery_image_beach)!!.asImage()

            "https://t1.nhentai.net/galleries/2/thumb.jpg" ->
                AppCompatResources.getDrawable(context, R.drawable.preview_gallery_image_motorcycle)!!.asImage()

            "https://t1.nhentai.net/galleries/3/thumb.jpg" ->
                AppCompatResources.getDrawable(context, R.drawable.preview_gallery_image_samurai)!!.asImage()

            "https://t1.nhentai.net/galleries/4/thumb.jpg" ->
                AppCompatResources.getDrawable(context, R.drawable.preview_gallery_image_cyberpunk)!!.asImage()

            else -> null
        }
    }

    private val handler = AsyncImagePreviewHandler { request ->
        val data = request.data
        if (data is String) {
            previewImage(data) ?: defaultPreviewImage
        } else {
            defaultPreviewImage
        }
    }

    override suspend fun handle(imageLoader: ImageLoader, request: ImageRequest): AsyncImagePainter.State {
        return handler.handle(imageLoader, request)
    }
}
