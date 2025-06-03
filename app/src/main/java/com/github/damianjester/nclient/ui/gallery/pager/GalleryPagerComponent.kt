package com.github.damianjester.nclient.ui.gallery.pager

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnResume
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryNotFound
import com.github.damianjester.nclient.core.GalleryPage
import com.github.damianjester.nclient.core.GalleryPageSaver
import com.github.damianjester.nclient.core.GalleryPageSharer
import com.github.damianjester.nclient.core.GalleryPagesFetcher
import com.github.damianjester.nclient.core.GalleryTitle
import com.github.damianjester.nclient.core.Result
import com.github.damianjester.nclient.db.GalleryRepository
import com.github.damianjester.nclient.net.NHentaiClientConnectionException
import com.github.damianjester.nclient.net.NHentaiClientException
import com.github.damianjester.nclient.net.NHentaiClientScrapeException
import com.github.damianjester.nclient.net.NHentaiClientSerializationException
import com.github.damianjester.nclient.ui.DefaultRootComponent
import com.github.damianjester.nclient.ui.gallery.pager.GalleryPagerComponent.PagesState
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface GalleryPagerComponent {
    val model: Value<Model>
    val config: DefaultRootComponent.Config.GalleryPager
    val snackbarMessage: Flow<SnackbarMessage>

    fun savePageToGallery(page: GalleryPage)

    fun sharePage(page: GalleryPage, withUrl: Boolean)

    fun navigateBack()

    data class Model(
        val pages: PagesState = PagesState.Loading,
        val galleryTitle: GalleryTitle? = null,
        val loading: Boolean = true,
    )

    sealed interface PagesState {
        data object Loading : PagesState

        data class Loaded(val pages: List<GalleryPage>) : PagesState

        sealed interface Error : PagesState {
            data class GalleryNotFound(val id: GalleryId) : Error

            data object NetworkConnection : Error

            data object Internal : Error
        }

        val pagesOrNull: List<GalleryPage>?
            get() = when (this) {
                is Loaded -> pages
                else -> null
            }
    }

    sealed interface SnackbarMessage {
        data object PageDownloaded : SnackbarMessage

        data object PageDownloadFailed : SnackbarMessage

        data object PageShareFailed : SnackbarMessage
    }
}

class DefaultGalleryPagerComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    override val config: DefaultRootComponent.Config.GalleryPager,
    private val onNavigateBack: () -> Unit,
    private val pagesFetcher: GalleryPagesFetcher,
    private val galleryRepository: GalleryRepository,
    private val pageSaver: GalleryPageSaver,
    private val pageSharer: GalleryPageSharer,
) : GalleryPagerComponent, ComponentContext by componentContext, KoinComponent {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate)
    private val _snackbarMessage = MutableSharedFlow<GalleryPagerComponent.SnackbarMessage>()
    override val snackbarMessage: Flow<GalleryPagerComponent.SnackbarMessage> = _snackbarMessage

    private val _model = MutableValue(GalleryPagerComponent.Model())
    override val model: Value<GalleryPagerComponent.Model>
        get() = _model

    init {
        doOnCreate {
            coroutineScope.launch {
                fetchGalleryPages()
            }
        }
        doOnResume {
            galleryRepository.selectGalleryTitle(config.id)
                .onEach { title -> _model.update { state -> state.copy(galleryTitle = title) } }
                .launchIn(coroutineScope)
        }
    }

    override fun savePageToGallery(page: GalleryPage) {
        coroutineScope.launch {
            val message = when (pageSaver.save(config.id, page)) {
                is GalleryPageSaver.Result.Success -> GalleryPagerComponent.SnackbarMessage.PageDownloaded
                is GalleryPageSaver.Result.Failure -> GalleryPagerComponent.SnackbarMessage.PageDownloadFailed
            }
            _snackbarMessage.emit(message)
        }
    }

    override fun sharePage(page: GalleryPage, withUrl: Boolean) {
        coroutineScope.launch {
            val result = pageSharer.share(config.id, page, withUrl)

            if (result is GalleryPageSharer.Result.Failure) {
                Log.e("component", "Failed to share gallery page: $result")
                _snackbarMessage.emit(GalleryPagerComponent.SnackbarMessage.PageShareFailed)
            }
        }
    }

    override fun navigateBack() = onNavigateBack()

    private suspend fun fetchGalleryPages() {
        try {
            val targetState = when (val result = pagesFetcher.fetch(config.id)) {
                is Result.Err -> {
                    require(result.cause is GalleryNotFound)
                    PagesState.Error.GalleryNotFound(config.id)
                }
                is Result.Ok -> PagesState.Loaded(result.value)
            }
            _model.update { state -> state.copy(pages = targetState) }
        } catch (ex: NHentaiClientException) {
            val error = when (ex) {
                is NHentaiClientConnectionException -> PagesState.Error.NetworkConnection
                is NHentaiClientScrapeException, is NHentaiClientSerializationException -> PagesState.Error.Internal
                else -> throw ex
            }

            _model.update { state -> state.copy(pages = error) }
        }
    }
}
