package com.github.damianjester.nclient.gallery.search

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.github.damianjester.nclient.GalleryGridItem
import com.github.damianjester.nclient.GalleryGridItemImage
import com.github.damianjester.nclient.GalleryId
import com.github.damianjester.nclient.GalleryLanguage
import com.github.damianjester.nclient.NHentaiHttpClient
import com.github.damianjester.nclient.core.GalleryPageLoader
import com.github.damianjester.nclient.core.GallerySearcher
import com.github.damianjester.nclient.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface GallerySearchComponent {
    val model: Value<Model>

    fun navigateToGallery(gallery: GalleryGridItem)

    data class Model(
        val galleriesState: GalleriesState = GalleriesState.Loading(userRefresh = false),
        val galleries: List<GalleryGridItem> = emptyList(),
    )

    sealed interface GalleriesState {
        data class Loading(val userRefresh: Boolean) : GalleriesState
        data class Error(val exception: Exception) : GalleriesState
        data object Loaded : GalleriesState
    }

}

class DefaultGallerySearchComponent(
    componentContext: ComponentContext,
    private val loader: GalleryPageLoader,
    private val searcher: GallerySearcher,
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
                loader.load(1)
            }

            lifecyleScope.launch {
                searcher.search()
                    .collect { galleries ->
                        _model.update { it.copy(galleries = galleries) }
                    }
            }
        }
    }

    override fun navigateToGallery(gallery: GalleryGridItem) {
        onNavigateGallery(gallery.id)
    }

}
