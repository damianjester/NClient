package com.github.damianjester.nclient.ui.gallery.pager

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.github.damianjester.nclient.core.GalleryDetailsObserver
import com.github.damianjester.nclient.core.GalleryPage
import com.github.damianjester.nclient.core.GalleryPageSaver
import com.github.damianjester.nclient.core.GalleryPageSharer
import com.github.damianjester.nclient.core.GalleryPagesObserver
import com.github.damianjester.nclient.ui.DefaultRootComponent
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class NewGalleryPagerComponent(
    componentContext: ComponentContext,
    override val config: DefaultRootComponent.Config.GalleryPager,
    private val onNavigateBack: () -> Unit,
    private val detailsObserver: GalleryDetailsObserver,
    private val pagesFetcher: GalleryPagesObserver,
    private val pageSaver: GalleryPageSaver,
    private val pageSharer: GalleryPageSharer,
) : GalleryPagerComponent, ComponentContext by componentContext, KoinComponent {
    private val scope = coroutineScope(Dispatchers.Default)
    private val _snackbarMessage = MutableSharedFlow<GalleryPagerComponent.SnackbarMessage>()
    override val snackbarMessage: Flow<GalleryPagerComponent.SnackbarMessage> = _snackbarMessage

    private val _model = MutableValue(GalleryPagerComponent.Model())
    override val model: Value<GalleryPagerComponent.Model>
        get() = _model

    init {
        doOnCreate {
            scope.launch {
                combine(
                    detailsObserver.details(config.id),
                    pagesFetcher.pages(config.id)
                ) { gallery, pages ->
                    GalleryPagerComponent.GalleryState.Loaded(
                        gallery = gallery,
                        pages = pages
                    )
                }.collect { state ->
                    _model.update { value ->
                        value.copy(
                            loading = false,
                            gallery = state
                        )
                    }
                }
            }
        }
    }

    override fun savePageToGallery(page: GalleryPage) {
        val gallery = when (val galleryState = model.value.gallery) {
            is GalleryPagerComponent.GalleryState.Loaded -> galleryState.gallery
            else -> {
                Log.w("component", "Can't download gallery page when gallery hasn't loaded yet.")
                return
            }
        }

        scope.launch {
            val message = when (pageSaver.save(gallery.id, page)) {
                is GalleryPageSaver.Result.Success -> GalleryPagerComponent.SnackbarMessage.PageDownloaded
                is GalleryPageSaver.Result.Failure -> GalleryPagerComponent.SnackbarMessage.PageDownloadFailed
            }
            _snackbarMessage.emit(message)
        }
    }

    override fun sharePage(page: GalleryPage, withUrl: Boolean) {
        val gallery = when (val states = model.value.gallery) {
            is GalleryPagerComponent.GalleryState.Loaded -> states.gallery
            else -> {
                Log.w("component", "Can't share gallery page when gallery hasn't loaded yet.")
                return
            }
        }

        scope.launch {
            val result = pageSharer.share(gallery.id, page, withUrl)

            if (result is GalleryPageSharer.Result.Failure) {
                Log.e("component", "Failed to share gallery page: $result")
                _snackbarMessage.emit(GalleryPagerComponent.SnackbarMessage.PageShareFailed)
            }
        }
    }

    override fun navigateBack() {
        onNavigateBack()
    }
}
