package com.github.damianjester.nclient.core

import android.content.Context
import com.github.damianjester.nclient.core.GalleryPageDownloader.Result
import com.github.damianjester.nclient.core.GalleryPageDownloader.Result.Failure
import com.github.damianjester.nclient.core.GalleryPageDownloader.Result.Success
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryPage
import com.github.damianjester.nclient.core.models.GalleryPageImages
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.Url
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface GalleryPageDownloader {
    suspend fun download(id: GalleryId, page: GalleryPage): Result

    sealed interface Result {
        data class Success(val file: File) : Result

        sealed interface Failure : Result {
            data object AlreadyDownloaded : Failure

            data class UnknownFileExtension(val filename: String) : Failure
        }
    }
}

class DefaultGalleryPageDownloader(
    private val context: Context,
    private val dispatchers: NClientDispatchers,
    private val logger: Logger,
    private val httpClient: HttpClient,
) : GalleryPageDownloader {
    override suspend fun download(id: GalleryId, page: GalleryPage): Result {
        val url = when (val image = page.image) {
            is GalleryPageImages.Remote -> image.remoteOriginal.url
            is GalleryPageImages.Local -> {
                logger.i(
                    LogTags.downloader,
                    "Page ${page.index + 1} from gallery $id is already downloaded."
                )
                return Failure.AlreadyDownloaded
            }
        }

        val filename = filename(url)
        if (filename == null) {
            val segmentOrUrl = url.segments.lastOrNull() ?: url.toString()
            logger.e(
                LogTags.downloader,
                "Unable to determine file extension for file from URL segment: $segmentOrUrl."
            )
            return Failure.UnknownFileExtension(segmentOrUrl)
        }

        val file = File(context.cacheDir, filename)
            .also {
                // TODO: Catch IO exceptions
                if (it.exists()) it.delete()
                it.createNewFile()
            }

        logger.i(LogTags.downloader, "Downloading page ${page.index + 1} from gallery $id.")

        withContext(dispatchers.IO) {
            // TODO: Move to client
            // TODO: Catch exceptions
            httpClient.prepareGet(url).execute { response ->
                val channel: ByteReadChannel = response.body<ByteReadChannel>()
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    file.appendBytes(packet.readByteArray())
                }
            }
        }

        logger.i(
            LogTags.downloader,
            "Downloaded page ${page.index + 1}, from gallery $id, to ${file.path}."
        )

        return Success(file)
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun filename(url: Url): String? {
        val stem = "tmp_${Uuid.random().toHexString()}"
        val extension = url.fileExtension ?: return null
        return "$stem.$extension"
    }

    private val Url.fileExtension
        get() = segments.last().split(".").lastOrNull()
}
