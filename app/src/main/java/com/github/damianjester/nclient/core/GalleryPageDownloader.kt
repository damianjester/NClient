package com.github.damianjester.nclient.core

import android.content.Context
import android.util.Log
import com.github.damianjester.nclient.core.GalleryPageDownloader.Result
import com.github.damianjester.nclient.core.GalleryPageDownloader.Result.Failure
import com.github.damianjester.nclient.core.GalleryPageDownloader.Result.Success
import com.github.damianjester.nclient.utils.NClientDispatchers
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
    private val httpClient: HttpClient,
) : GalleryPageDownloader {
    override suspend fun download(id: GalleryId, page: GalleryPage): Result {
        val url = when (val image = page.image) {
            is GalleryPageImage.Remote -> image.originalUrl
            is GalleryPageImage.Local -> return Failure.AlreadyDownloaded
        }

        val filename = filename(url)
            ?: return Failure.UnknownFileExtension(url.segments.lastOrNull() ?: url.toString())

        val file = File(context.cacheDir, filename)
            .also {
                if (it.exists()) it.delete()
                it.createNewFile()
            }

        Log.i("downloader", "Downloading page ${page.index + 1} from gallery #$id")

        withContext(dispatchers.IO) {
            httpClient.prepareGet(url).execute { response ->
                val channel: ByteReadChannel = response.body<ByteReadChannel>()
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    file.appendBytes(packet.readByteArray())
                }
            }
        }

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
