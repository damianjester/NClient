package com.github.damianjester.nclient.ui.gallery.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.github.damianjester.nclient.core.GallerySearchItem
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GallerySearchPager
import com.github.damianjester.nclient.core.GallerySearchObserver
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

interface GallerySearchComponent {
    val model: Value<Model>

    fun navigateToGallery(gallery: GallerySearchItem)

    data class Model(
        val galleriesState: GalleriesState = GalleriesState.Loading(userRefresh = false),
        val galleries: List<GallerySearchItem> = emptyList(),
    )

    sealed interface GalleriesState {
        data class Loading(val userRefresh: Boolean) : GalleriesState
        data class Error(val exception: Exception) : GalleriesState
        data object Loaded : GalleriesState
    }

}

class DefaultGallerySearchComponent(
    componentContext: ComponentContext,
    private val pager: GallerySearchPager,
    private val fetcher: GallerySearchObserver,
    val onNavigateGallery: (GalleryId) -> Unit,
) : GallerySearchComponent, ComponentContext by componentContext, KoinComponent {

    private val lifecyleScope = coroutineScope(Dispatchers.Default)
    private val _model = MutableValue(GallerySearchComponent.Model())
    override val model: Value<GallerySearchComponent.Model> = _model

    init {
        lifecycle.doOnCreate {
            // TODO: Fetch galleries through the domain layer
            // TODO: Persist galleries to local database
            // TODO: Observe galleries through the domain layer
            // TODO: Preload tags? Refresh tags?

            lifecyleScope.launch {
                pager.fetch(1)
            }

            lifecyleScope.launch {
                fetcher.galleries()
                    .collect { galleries ->
                        _model.update { it.copy(galleries = galleries) }
                    }
            }
        }
    }

    override fun navigateToGallery(gallery: GallerySearchItem) {
        onNavigateGallery(gallery.id)
    }

}
