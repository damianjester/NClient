package com.github.damianjester.nclient.core

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.github.damianjester.nclient.core.GalleryPageSharer.GalleryPageShare
import com.github.damianjester.nclient.core.GalleryPageSharer.Result.Failure
import com.github.damianjester.nclient.core.GalleryPageSharer.Result.Success
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.utils.fileExtension
import com.github.damianjester.nclient.utils.filenameForExternalStorage
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.File

interface GalleryPageSharer {
    val shares: Flow<GalleryPageShare>

    suspend fun share(id: GalleryId, page: GalleryPage, withUrl: Boolean): Result

    data class GalleryPageShare(
        val fileUri: Uri,
        val mineType: String,
        val webpageUrl: Url?,
    )

    sealed interface Result {
        data object Success : Result

        sealed interface Failure : Result {
            data class UnknownFileExtension(val filename: String) : Failure

            data class MineTypeUnknown(val filename: String) : Failure

            data class DownloaderFailed(val reason: GalleryPageDownloader.Result.Failure) : Failure

            data object NoPagesFound : Failure

            data class FileProviderError(val exception: Exception) : Failure
        }
    }
}

class DefaultGalleryPageSharer(
    private val context: Context,
    private val galleryRepository: GalleryRepository,
    private val downloader: GalleryPageDownloader,
) : GalleryPageSharer {
    private val _shares = MutableSharedFlow<GalleryPageShare>()
    override val shares: Flow<GalleryPageShare>
        get() = _shares

    override suspend fun share(
        id: GalleryId,
        page: GalleryPage,
        withUrl: Boolean,
    ): GalleryPageSharer.Result {
        val file = when (val result = pageFile(id, page)) {
            is FileOrFailure.Failure -> return result.reason
            is FileOrFailure.Success -> result.file
        }

        val uri = when (val result = getFileUri(file, id, page)) {
            is UriOrFailure.Failure -> return result.reason
            is UriOrFailure.Success -> result.uri
        }

        val mimeType = file.fileExtension
            ?.let { extension -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) }
            ?: return Failure.MineTypeUnknown(file.name)

        _shares.emit(
            GalleryPageShare(
                fileUri = uri,
                mineType = mimeType,
                webpageUrl = if (withUrl) page.webpageUrl(id) else null
            )
        )

        return Success
    }

    private sealed interface FileOrFailure {
        data class Success(val file: File) : FileOrFailure

        data class Failure(val reason: GalleryPageSharer.Result.Failure) : FileOrFailure
    }

    private sealed interface UriOrFailure {
        data class Success(val uri: Uri) : UriOrFailure

        data class Failure(val reason: GalleryPageSharer.Result.Failure) : UriOrFailure
    }

    private suspend fun pageFile(id: GalleryId, page: GalleryPage) =
        when (val image = page.image) {
            is GalleryPageImage.Local -> FileOrFailure.Success(image.originalFile)
            is GalleryPageImage.Remote -> {
                when (val result = downloader.download(id, page)) {
                    is GalleryPageDownloader.Result.Failure ->
                        FileOrFailure.Failure(Failure.DownloaderFailed(result))
                    is GalleryPageDownloader.Result.Success -> FileOrFailure.Success(result.file)
                }
            }
        }

    private suspend fun getFileUri(file: File, id: GalleryId, page: GalleryPage): UriOrFailure {
        val pageCount = galleryRepository.countPagesForGallery(id)
        if (pageCount < 1) {
            return UriOrFailure.Failure(Failure.NoPagesFound)
        }

        val fileExtension = file.fileExtension
            ?: return UriOrFailure.Failure(Failure.UnknownFileExtension(file.name))
        val displayName = page.filenameForExternalStorage(id, pageCount, fileExtension)

        return try {
            val uri = FileProvider
                .getUriForFile(context, context.packageName + ".provider", file, displayName)

            UriOrFailure.Success(uri)
        } catch (ex: IllegalArgumentException) {
            UriOrFailure.Failure(Failure.FileProviderError(ex))
        }
    }
}
