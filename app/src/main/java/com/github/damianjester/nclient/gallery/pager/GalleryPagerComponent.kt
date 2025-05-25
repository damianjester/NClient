package com.github.damianjester.nclient.gallery.pager

import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.DefaultRootComponent
import com.github.damianjester.nclient.Gallery
import com.github.damianjester.nclient.GalleryPage
import com.github.damianjester.nclient.api.components.GenericGallery
import com.github.damianjester.nclient.coroutineScope
import com.github.damianjester.nclient.gallery.pager.GalleryPagerComponent.SnackbarMessage
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface GalleryPagerComponent {

    val model: Value<Model>
    val config: DefaultRootComponent.Config.GalleryPager
    val snackbarMessage: Flow<SnackbarMessage>

    fun savePageToGallery(page: GalleryPage)

    fun sharePage(page: GalleryPage, withUrl: Boolean)

    fun navigateBack()

    data class Model(
        val loading: Boolean = true,
        val gallery: GalleryState = GalleryState.Loading,
    )

    sealed interface GalleryState {
        data object Loading : GalleryState
        data class Loaded(val gallery: Gallery, val pages: List<GalleryPage>) : GalleryState
        data object NotFound : GalleryState
    }

    sealed interface SnackbarMessage {
        data object PageDownloaded : SnackbarMessage
        data object PageDownloadFailed : SnackbarMessage
        data object PageShareFailed : SnackbarMessage
    }

//    data class Gallery(
//        val id: Long,
//        val title: String,
//        val pages: List<GalleryPage>,
//    )
//
//    data class GalleryPage(
//        val index: Int,
//        val image: GalleryPageImage,
//    )
//
//    sealed interface GalleryPageImage {
//        val webpageUrl: String
//
//        data class Remote(override val webpageUrl: String, val url: String) : GalleryPageImage
//        data class Local(override val webpageUrl: String, val file: File) : GalleryPageImage
//    }

}

class DefaultGalleryPagerComponent(
    genericGallery: GenericGallery?,
    componentContext: ComponentContext,
    val context: Context,
) : GalleryPagerComponent, ComponentContext by componentContext, KoinComponent {

    override val config: DefaultRootComponent.Config.GalleryPager
        get() = TODO("Not yet implemented")

    private val _snackbarMessage = MutableSharedFlow<SnackbarMessage>()
    override val snackbarMessage: Flow<SnackbarMessage> = _snackbarMessage

    private val scope = coroutineScope(Dispatchers.Default)
    private val httpClient: HttpClient by inject()

    override val model = MutableValue(
        GalleryPagerComponent.Model(
            loading = false,
            gallery = if (genericGallery == null) {
                GalleryPagerComponent.GalleryState.NotFound
            } else {
                GalleryPagerComponent.GalleryState.Loading
            }
        )
    )

//    init {
//        if (genericGallery != null) {
//            componentContext.doOnCreate {
//                model.update {
//                    it.copy(
//                        loading = false,
//                        gallery = GalleryPagerComponent.GalleryState.Loaded(genericGallery.toModel())
//                    )
//                }
//            }
//        } else {
//            model.update {
//                it.copy(gallery = GalleryPagerComponent.GalleryState.NotFound)
//            }
//        }
//    }

    override fun savePageToGallery(page: GalleryPage) {
//
//        val gallery = when (val galleryState = model.value.gallery) {
//            is GalleryPagerComponent.GalleryState.Loaded -> galleryState.gallery
//            else -> return
//        }
//
//        scope.launch {
//            try {
//                val file = when (page.image) {
//                    is GalleryPagerComponent.GalleryPageImage.Remote -> downloadGalleryPage(
//                        gallery,
//                        page
//                    )
//
//                    is GalleryPagerComponent.GalleryPageImage.Local -> page.image.file
//                }
//                saveGalleryPageToExternalStorage(gallery, page, file)
//                cleanupAfterGalleryPageSave(page, file)
//                _snackbarMessage.emit(GalleryPagerComponent.SnackbarMessage.PageDownloaded)
//            } catch (ex: Exception) {
//                Log.e(LogUtility.LOGTAG, "Failed to save page to external storage.", ex)
//                _snackbarMessage.emit(GalleryPagerComponent.SnackbarMessage.PageDownloadFailed)
//            }
//        }
    }

    override fun sharePage(page: GalleryPage, includeUrl: Boolean) {
//
//        val gallery = when (val galleryState = model.value.gallery) {
//            is GalleryPagerComponent.GalleryState.Loaded -> galleryState.gallery
//            else -> return
//        }
//
//        scope.launch {
//            try {
//                val file = when (val image = page.image) {
//                    is GalleryPagerComponent.GalleryPageImage.Local -> image.file
//                    is GalleryPagerComponent.GalleryPageImage.Remote ->
//                        downloadGalleryPage(gallery, page)
//                }
//
//                val fileExtension = file.name.fileExtension()
//                val displayName = page.filename(gallery, fileExtension)
//                val uri = FileProvider
//                    .getUriForFile(context, context.packageName + ".provider", file, displayName)
//
//                val shareIntent: Intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    if (includeUrl) putExtra(Intent.EXTRA_TEXT, page.image.webpageUrl)
//                    type = MimeTypeMap.getSingleton()
//                        .getMimeTypeFromExtension(fileExtension)
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                }
//
//                withContext(Dispatchers.Main) {
//                    context.startActivity(Intent.createChooser(shareIntent, null))
//                }
//            } catch (ex: Exception) {
//                Log.e(LogUtility.LOGTAG, "Failed to share gallery page.", ex)
//                _snackbarMessage.emit(GalleryPagerComponent.SnackbarMessage.PageShareFailed)
//            }
//        }
    }

    override fun navigateBack() {
        TODO("Not yet implemented")
    }

//    @OptIn(ExperimentalUuidApi::class)
//    private suspend fun downloadGalleryPage(
//        gallery: GalleryPagerComponent.Gallery,
//        page: GalleryPagerComponent.GalleryPage,
//    ): File = withContext(Dispatchers.IO) {
//
//        val url = when (val image = page.image) {
//            is GalleryPagerComponent.GalleryPageImage.Local ->
//                error("Gallery page already downloaded to internal storage.")
//
//            is GalleryPagerComponent.GalleryPageImage.Remote -> image.url
//        }
//
//        val fileExtension = Url(url).segments.last().fileExtension()
//        val file = File(context.cacheDir, "tmp_${Uuid.random().toHexString()}.$fileExtension")
//            .also {
//                if (it.exists()) it.delete()
//                it.createNewFile()
//            }
//
//        Log.i(LogUtility.LOGTAG, "Downloading page ${page.index + 1} from gallery #${gallery.id}")
//
//        httpClient.prepareGet(url).execute { response ->
//            val channel: ByteReadChannel = response.body<ByteReadChannel>()
//            while (!channel.isClosedForRead) {
//                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
//                file.appendBytes(packet.readByteArray())
//            }
//        }
//
//        file
//    }

//    private suspend fun saveGalleryPageToExternalStorage(
//        gallery: GalleryPagerComponent.Gallery,
//        page: GalleryPagerComponent.GalleryPage,
//        galleryPageImageFile: File,
//    ) = withContext(Dispatchers.Default) {
//
//        Log.i(
//            LogUtility.LOGTAG,
//            "Saving page ${page.index + 1} from gallery #${gallery.id} to media store"
//        )
//
//        val contentResolver = context.contentResolver
//        val fileExtension = galleryPageImageFile.name.fileExtension()
//        val filename = page.filename(gallery, fileExtension)
//        val mimeType = MimeTypeMap.getSingleton()
//            .getMimeTypeFromExtension(fileExtension)
//
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
//            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
//        }
//
//        val uri = requireNotNull(contentResolver.insertCompat(contentValues)) {
//            "Content resolver returned a null Uri. Unable to save gallery page to external storage."
//        }
//
//        val fileDescriptor = requireNotNull(contentResolver.openFileDescriptor(uri, "w")) {
//            "Content resolver returned a null FileDescriptor. Unable to save gallery page to external storage."
//        }
//
//        fileDescriptor.use { pfd ->
//            FileOutputStream(pfd.fileDescriptor).use { output ->
//                galleryPageImageFile.inputStream().use { input ->
//                    input.copyTo(output)
//                }
//            }
//        }
//
//        Log.i(
//            LogUtility.LOGTAG,
//            "Saved page ${page.index + 1} from gallery #${gallery.id} to media store"
//        )
//    }
//
//    private fun ContentResolver.insertCompat(contentValues: ContentValues): Uri? {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            insert(
//                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
//                contentValues
//            )
//        } else {
//            insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//        }
//    }
//
//    private fun cleanupAfterGalleryPageSave(
//        page: GalleryPagerComponent.GalleryPage,
//        file: File,
//    ) {
//        if (page.image is GalleryPagerComponent.GalleryPageImage.Remote) {
//            file.delete()
//        }
//    }
//
//    private fun String.fileExtension() = split(".").last()
//
//    private val Int.length
//        get() = when (this) {
//            0 -> 1
//            else -> log10(abs(toDouble())).toInt() + 1
//        }
//
//    private fun GalleryPagerComponent.GalleryPage.filename(
//        gallery: GalleryPagerComponent.Gallery,
//        fileExtension: String,
//    ): String {
//        val paddedPageNumber = (index + 1).toString().padStart(gallery.pages.size.length, '0')
//        return "${gallery.id}_$paddedPageNumber.$fileExtension"
//    }
//
}
