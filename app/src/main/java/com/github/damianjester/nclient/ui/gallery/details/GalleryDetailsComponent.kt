package com.github.damianjester.nclient.ui.gallery.details

import android.content.ClipData
import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.doOnStart
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.GalleryDetailsFetcher
import com.github.damianjester.nclient.core.GalleryNotFound
import com.github.damianjester.nclient.core.LinkSharer
import com.github.damianjester.nclient.core.models.GalleryDetails
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.GalleryTag
import com.github.damianjester.nclient.core.models.Result
import com.github.damianjester.nclient.net.NHentaiClientConnectionException
import com.github.damianjester.nclient.net.NHentaiClientException
import com.github.damianjester.nclient.net.NHentaiClientScrapeException
import com.github.damianjester.nclient.net.NHentaiClientSerializationException
import com.github.damianjester.nclient.net.NHentaiUrl
import com.github.damianjester.nclient.ui.DefaultRootComponent
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsComponent.GalleryState
import com.github.damianjester.nclient.ui.gallery.history.HistoryTrackerComponent
import com.github.damianjester.nclient.ui.gallery.history.HistoryTrackerState
import com.github.damianjester.nclient.ui.gallery.history.HistoryTrackerStateEntity
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.clipboardManager
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val HISTORY_SAVED_STATE_KEY = "HISTORY_SAVED_STATE"

interface GalleryDetailsComponent {
    val config: DefaultRootComponent.Config.GalleryDetails
    val model: Value<Model>

    fun copyToClipboard(metadata: MetadataCopy)

    fun setGalleryFavoriteStatus(favorite: Boolean)

    fun toggleGridMode()

    fun shareGallery()

    fun navigateToPage(index: Int)

    fun navigateToComments()

    fun navigateRelated(id: GalleryId)

    fun navigateBack()

    data class Model(
        val gridMode: GridMode = GridMode.TWO_COLUMNS,
        val gallery: GalleryState = GalleryState.Loading,
        val isFavorite: Boolean = false,
    )

    sealed interface GalleryState {
        data object Loading : GalleryState

        data class Loaded(val details: GalleryDetails) : GalleryState

        sealed interface Error : GalleryState {
            data class NotFound(val id: GalleryId) : Error

            data object NetworkConnection : Error

            data object Internal : Error
        }
    }

    sealed interface MetadataCopy {
        data class Id(val id: GalleryId) : MetadataCopy

        data class Tag(val tag: GalleryTag) : MetadataCopy

        data class Title(val value: String) : MetadataCopy
    }

    enum class GridMode(val count: Int) {
        ONE_COLUMN(1), TWO_COLUMNS(2), THREE_COLUMNS(3), FOUR_COLUMNS(4)
    }
}

class DefaultGalleryDetailsComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    override val config: DefaultRootComponent.Config.GalleryDetails,
    private val onNavigatePage: (Int) -> Unit,
    private val onNavigateComments: (GalleryId) -> Unit,
    private val onNavigateRelated: (GalleryId) -> Unit,
    private val onNavigateBack: () -> Unit,
    private val applicationContext: Context,
    private val galleryFetcher: GalleryDetailsFetcher,
    private val linkSharer: LinkSharer,
) : GalleryDetailsComponent, HistoryTrackerComponent, ComponentContext by componentContext {
    override val trackerStateEntity: HistoryTrackerStateEntity =
        instanceKeeper.getOrCreate {
            stateKeeper.consume(key = HISTORY_SAVED_STATE_KEY, strategy = HistoryTrackerState.serializer())
                ?.let { savedState -> HistoryTrackerStateEntity(savedState) }
                ?: HistoryTrackerStateEntity(config.id)
        }

    private val _model = MutableValue(GalleryDetailsComponent.Model())
    override val model: Value<GalleryDetailsComponent.Model>
        get() = _model

    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    init {
        stateKeeper.register(
            key = HISTORY_SAVED_STATE_KEY,
            strategy = HistoryTrackerState.serializer(),
            supplier = trackerStateEntity::state
        )
        doOnStart(isOneTime = true) {
            coroutineScope.launch { fetchGallery() }
        }
    }

    override fun copyToClipboard(metadata: GalleryDetailsComponent.MetadataCopy) {
        val content = when (metadata) {
            is GalleryDetailsComponent.MetadataCopy.Id -> metadata.id.toString()
            is GalleryDetailsComponent.MetadataCopy.Tag -> metadata.tag.name
            is GalleryDetailsComponent.MetadataCopy.Title -> metadata.value
        }

        val clipboardManager = applicationContext.clipboardManager
        val label = when (metadata) {
            is GalleryDetailsComponent.MetadataCopy.Id ->
                applicationContext.getString(R.string.clipboard_gallery_id)

            is GalleryDetailsComponent.MetadataCopy.Tag ->
                applicationContext.getString(R.string.clipboard_gallery_tag)

            is GalleryDetailsComponent.MetadataCopy.Title ->
                applicationContext.getString(R.string.clipboard_gallery_title)
        }

        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, content))
    }

    override fun setGalleryFavoriteStatus(favorite: Boolean) {
        doOnLoaded {
            TODO("Not yet implemented")
        }
    }

    override fun toggleGridMode() {
        _model.update { state ->
            val entries = GalleryDetailsComponent.GridMode.entries
            val newGridMode = if (entries.size > (entries.indexOf(state.gridMode) + 1)) {
                entries[entries.indexOf(state.gridMode) + 1]
            } else {
                GalleryDetailsComponent.GridMode.ONE_COLUMN
            }

            state.copy(gridMode = newGridMode)
        }
    }

    override fun shareGallery() = doOnLoaded { loaded ->
        coroutineScope.launch {
            val gallery = loaded.details.gallery
            val url = NHentaiUrl.galleryWebPage(gallery.id)
            linkSharer.share(url)
        }
    }

    override fun navigateToPage(index: Int) = onNavigatePage(index)

    override fun navigateToComments() {
        doOnLoaded {
            onNavigateComments(config.id)
        }
    }

    override fun navigateRelated(id: GalleryId) = onNavigateRelated(id)

    override fun navigateBack() = onNavigateBack()

    private suspend fun fetchGallery() {
        try {
            val targetState = when (val result = galleryFetcher.fetch(config.id)) {
                is Result.Err -> {
                    require(result.cause is GalleryNotFound)
                    GalleryState.Error.NotFound(config.id)
                }

                is Result.Ok -> GalleryState.Loaded(result.value)
            }

            _model.update { state -> state.copy(gallery = targetState) }
        } catch (ex: NHentaiClientException) {
            val error = when (ex) {
                is NHentaiClientConnectionException -> GalleryState.Error.NetworkConnection
                is NHentaiClientScrapeException, is NHentaiClientSerializationException ->
                    GalleryState.Error.Internal

                else -> throw ex
            }

            _model.update { state -> state.copy(gallery = error) }
        }
    }

    private fun doOnLoaded(block: (GalleryState.Loaded) -> Unit) {
        val state = _model.value.gallery
        if (state is GalleryState.Loaded) {
            block(state)
        }
    }
}
