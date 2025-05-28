package com.github.damianjester.nclient.ui.gallery.details

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnResume
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.Gallery
import com.github.damianjester.nclient.core.GalleryDetailsFetcher
import com.github.damianjester.nclient.core.GalleryDetailsObserver
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryPage
import com.github.damianjester.nclient.core.GalleryPagesObserver
import com.github.damianjester.nclient.core.GalleryTag
import com.github.damianjester.nclient.core.GalleryTagType
import com.github.damianjester.nclient.core.GalleryTagsObserver
import com.github.damianjester.nclient.core.RelatedGalleriesObserver
import com.github.damianjester.nclient.core.RelatedGallery
import com.github.damianjester.nclient.ui.DefaultRootComponent
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.clipboardManager
import com.github.damianjester.nclient.utils.coroutineScope
import com.github.damianjester.nclient.utils.legacyClipboardManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

interface GalleryDetailsComponent {
    val config: DefaultRootComponent.Config.GalleryDetails
    val model: Value<Model>

    fun copyToClipboard(metadata: MetadataCopy)

    fun setGalleryFavoriteStatus(favorite: Boolean)

    fun toggleGridMode()

    fun navigateToPage(index: Int)

    fun navigateToComments()

    fun navigateRelated(id: GalleryId)

    fun navigateBack()

    data class Model(
        val gridMode: GridMode = GridMode.TWO_COLUMNS,
        val galleryState: GalleryState = GalleryState.Loading,
    )

    sealed interface GalleryState {
        data object Loading : GalleryState

        data class Loaded(
            val gallery: Gallery,
            val pages: List<GalleryPage>,
            val tags: GalleryTags,
            val related: List<RelatedGallery>,
        ) : GalleryState

        data object NotFound : GalleryState
    }

    data class GalleryTags(
        val all: List<GalleryTag>,
        val parody: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Parody) },
        val character: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Character) },
        val general: List<GalleryTag> = all.filter { (it.type == GalleryTagType.General) },
        val artist: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Artist) },
        val group: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Group) },
        val language: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Language) },
        val category: List<GalleryTag> = all.filter { (it.type == GalleryTagType.Category) },
    )

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
    private val galleryObserver: GalleryDetailsObserver,
    private val pagesObserver: GalleryPagesObserver,
    private val tagsObserver: GalleryTagsObserver,
    private val relatedObserver: RelatedGalleriesObserver,
) : GalleryDetailsComponent, ComponentContext by componentContext {
    private val lifecycleScope = coroutineScope(dispatchers.Default)
    private val _model = MutableValue(GalleryDetailsComponent.Model())
    override val model: Value<GalleryDetailsComponent.Model>
        get() = _model

    init {

        // TODO: GallerySearchItem could not exist in DB yet when deep linking, check and load if needed

        doOnResume {
            lifecycleScope.launch {

                val result = galleryFetcher.fetch(config.id)
                if (result is GalleryDetailsFetcher.Result.Failure) {
                    Log.e("component", "Failed to load gallery details", result.exception)
                    // TODO: Update state to reflect error state
                }

                combine(
                    galleryObserver.details(config.id),
                    pagesObserver.pages(config.id),
                    tagsObserver.tags(config.id),
                    relatedObserver.galleries(config.id)
                ) { gallery, pages, tags, related ->
                    GalleryDetailsComponent.GalleryState.Loaded(
                        gallery,
                        pages,
                        GalleryDetailsComponent.GalleryTags(tags),
                        related
                    )
                }.collect { state ->
                    _model.update {
                        it.copy(
                            galleryState = state
                        )
                    }
                }
            }
        }
    }

    override fun copyToClipboard(metadata: GalleryDetailsComponent.MetadataCopy) {
        val content = when (metadata) {
            is GalleryDetailsComponent.MetadataCopy.Id -> metadata.id.toString()
            is GalleryDetailsComponent.MetadataCopy.Tag -> metadata.tag.name
            is GalleryDetailsComponent.MetadataCopy.Title -> metadata.value
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        } else {
            applicationContext.legacyClipboardManager.text = content
        }
    }

    override fun setGalleryFavoriteStatus(favorite: Boolean) {
        TODO("Not yet implemented")
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

    override fun navigateToPage(index: Int) = onNavigatePage(index)

    override fun navigateToComments() = onNavigateComments(config.id)

    override fun navigateRelated(id: GalleryId) = onNavigateRelated(id)

    override fun navigateBack() = onNavigateBack()
}
