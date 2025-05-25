package com.github.damianjester.nclient.core

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.core.GalleryPageSaver.Result.Failure
import com.github.damianjester.nclient.core.GalleryPageSaver.Result.Success
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.utils.fileExtension
import com.github.damianjester.nclient.utils.filenameForExternalStorage
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

interface GalleryPageSaver {

    suspend fun save(id: GalleryId, page: GalleryPage): Result

    sealed interface Result {
        data object Success : Result
        sealed interface Failure : Result {
            data class UnknownFileExtension(val filename: String) : Failure
            data class MineTypeUnknown(val filename: String) : Failure
            data class DownloaderFailed(val reason: GalleryPageDownloader.Result.Failure) : Failure
            data object NoPagesFound : Failure
            data object NullContentUri : Failure
            data object NullFileDescriptor : Failure
            data class CacheFileDeletionFailed(val exception: IOException) : Failure
        }
    }
}

class DefaultGalleryPageSaver(
    private val context: Context,
    private val dispatchers: NClientDispatchers,
    private val downloader: GalleryPageDownloader,
    private val galleryRepository: GalleryRepository,
) : GalleryPageSaver {

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    override suspend fun save(
        id: GalleryId,
        page: GalleryPage,
    ): GalleryPageSaver.Result {

        val file = when (page.image) {
            is GalleryPageImage.Remote -> when (val result = downloader.download(id, page)) {
                is GalleryPageDownloader.Result.Failure -> return Failure.DownloaderFailed(result)
                is GalleryPageDownloader.Result.Success -> result.file
            }

            is GalleryPageImage.Local -> page.image.originalFile
        }

        Log.i(
            "saver",
            "Saving page ${page.index + 1} from gallery $id to media store"
        )

        val pageCount = galleryRepository.countPagesForGallery(id)
        if (pageCount < 1) {
            return Failure.NoPagesFound
        }

        val fileExtension = file.fileExtension
            ?: return Failure.UnknownFileExtension(file.name)
        val filename = page.filenameForExternalStorage(id, pageCount, fileExtension)

        val mimeType = fileExtension
            .let { extension -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) }
            ?: return Failure.MineTypeUnknown(file.name)

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        }

        val uri = contentResolver.insertCompat(contentValues)
            ?: return Failure.NullContentUri
//        "Content resolver returned a null Uri. Unable to save gallery page to external storage."

        val fileDescriptor = contentResolver.openFileDescriptor(uri, "w")
            ?: return Failure.NullFileDescriptor
//            "Content resolver returned a null FileDescriptor. Unable to save gallery page to external storage."

        withContext(dispatchers.IO) {
            fileDescriptor.use { pfd ->
                FileOutputStream(pfd.fileDescriptor).use { output ->
                    file.inputStream().use { input ->
                        input.copyTo(output)
                    }
                }
            }
        }

        Log.i(
            "saver",
            "Saved page ${page.index + 1} from gallery $id to media store"
        )

        try {
            cleanupAfterGalleryPageSave(page, file)
        } catch (ex: IOException) {
            return Failure.CacheFileDeletionFailed(ex)
        }

//            Log.e(LogUtility.LOGTAG, "Failed to save page to external storage.", ex)

        return Success
    }

    private fun ContentResolver.insertCompat(contentValues: ContentValues): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            insert(
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                contentValues
            )
        } else {
            insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }
    }

    private fun cleanupAfterGalleryPageSave(
        page: GalleryPage,
        file: File,
    ) {
        if (page.image is GalleryPageImage.Remote) {
            file.delete()
        }
    }

}
