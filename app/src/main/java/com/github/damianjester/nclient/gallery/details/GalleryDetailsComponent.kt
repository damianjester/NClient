package com.github.damianjester.nclient.gallery.details

import android.content.ClipData
import android.content.Context
import android.os.Build
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.github.damianjester.nclient.DefaultRootComponent
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.api.components.Gallery
import com.github.damianjester.nclient.api.components.GenericGallery
import com.github.damianjester.nclient.api.enums.Language
import com.github.damianjester.nclient.api.enums.TagType
import com.github.damianjester.nclient.clipboardManager
import com.github.damianjester.nclient.legacyClipboardManager
import com.github.damianjester.nclient.settings.Favorites
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File

interface GalleryDetailsComponent {

    val config: DefaultRootComponent.Config.GalleryDetails
    val model: Value<Model>

    fun copyToClipboard(metadata: MetadataCopy)

    fun setGalleryFavoriteStatus(favorite: Boolean)

    fun toggleGridMode()

    fun navigateBack()

    data class Model(
        val gridMode: GridMode = GridMode.TWO_COLUMNS,
        val galleryState: GalleryState = GalleryState.Loading,
    )

    sealed interface GalleryState {
        data object Loading : GalleryState
        data class Loaded(val gallery: Gallery) : GalleryState
        data object NotFound : GalleryState
    }

    data class Gallery(
        val id: Long,
        val title: String,
        val tags: GalleryTags,
        val pages: List<GalleryPage>,
        val updated: LocalDateTime,
        val favoriteCount: Int,
        val isFavorite: Boolean,
        val related: List<RelatedGallery>
    )

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

    data class GalleryTag(
        val id: Long,
        val name: String,
        val type: GalleryTagType
    )

    enum class GalleryTagType {
        Parody, Character, General, Artist, Group, Language, Category, Unknown
    }

    data class GalleryPage(
        val index: Int,
        val image: GalleryPageImage,
    )

    sealed interface GalleryPageImage {
        val webpageUrl: String

        data class Remote(
            override val webpageUrl: String,
            val originalUrl: String,
            val thumbnailUrl: String
        ) : GalleryPageImage

        data class Local(
            override val webpageUrl: String,
            val file: File,
        ) : GalleryPageImage
    }

    sealed interface MetadataCopy {
        data class Id(val id: Long) : MetadataCopy
        data class Tag(val tag: GalleryTag) : MetadataCopy
        data class Title(val value: String) : MetadataCopy
    }

    enum class GridMode(val count: Int) {
        ONE_COLUMN(1), TWO_COLUMNS(2), THREE_COLUMNS(3), FOUR_COLUMNS(4)
    }

    data class RelatedGallery(
        val id: Long,
        val title: String,
        val coverImageUrl: String,
        val locale: GalleryLocale
    )

    sealed interface GalleryLocale {
        data object English : GalleryLocale
        data object Chinese : GalleryLocale
        data object Japanese : GalleryLocale
        data class Unknown(val locale: String) : GalleryLocale
    }

}

class NewGalleryDetailsComponent(
    componentContext: ComponentContext,
    override val config: DefaultRootComponent.Config.GalleryDetails,
    private val onPageClick: (Long, Int) -> Unit,
    private val onNavigateBack: () -> Unit
) : GalleryDetailsComponent, ComponentContext by componentContext {
    override val model = MutableValue(GalleryDetailsComponent.Model())

    override fun copyToClipboard(metadata: GalleryDetailsComponent.MetadataCopy) {
        TODO("Not yet implemented")
    }

    override fun setGalleryFavoriteStatus(favorite: Boolean) {
        TODO("Not yet implemented")
    }

    override fun toggleGridMode() {
        TODO("Not yet implemented")
    }

    override fun navigateBack() {
        onNavigateBack()
    }
}

class DefaultGalleryDetailsComponent(
    componentContext: ComponentContext,
    private val gallery: GenericGallery?,
    private val applicationContext: Context,
) : GalleryDetailsComponent, ComponentContext by componentContext {

    override val config: DefaultRootComponent.Config.GalleryDetails
        get() = TODO("Not yet implemented")
    override val model = MutableValue(GalleryDetailsComponent.Model())

    init {
        loadGallery(gallery)
        // TODO: If gallery is not local, add to history
    }

    private fun loadGallery(gallery: GenericGallery?) {
        if (gallery == null) {
            model.update { it.copy(galleryState = GalleryDetailsComponent.GalleryState.NotFound) }
            return
        }

        val pages: List<GalleryDetailsComponent.GalleryPage> = if (gallery.isLocal) {
            buildList {
                repeat(gallery.pageCount) { i ->
                    add(
                        GalleryDetailsComponent.GalleryPage(
                            index = i,
                            image = GalleryDetailsComponent.GalleryPageImage.Local(
                                webpageUrl = gallery.sharePageUrl(i).toString(),
                                file = gallery.galleryFolder.getPage(i + 1)
                            )
                        )
                    )
                }
            }
        } else {
            buildList {
                val gal = gallery as Gallery
                repeat(gal.pageCount) { i ->
                    add(
                        GalleryDetailsComponent.GalleryPage(
                            index = i,
                            image = GalleryDetailsComponent.GalleryPageImage.Remote(
                                webpageUrl = gal.sharePageUrl(i).toString(),
                                originalUrl = gal.getPageUrl(i).toString(),
                                thumbnailUrl = gal.getLowPage(i).toString(),
                            )
                        )
                    )
                }
            }
        }

        val tags = gallery.galleryData.tags
            .allTagsList
            .map { tag ->

                val type = when (tag.type) {
                    TagType.PARODY -> GalleryDetailsComponent.GalleryTagType.Parody
                    TagType.CHARACTER -> GalleryDetailsComponent.GalleryTagType.Character
                    TagType.TAG -> GalleryDetailsComponent.GalleryTagType.General
                    TagType.ARTIST -> GalleryDetailsComponent.GalleryTagType.Artist
                    TagType.GROUP -> GalleryDetailsComponent.GalleryTagType.Group
                    TagType.LANGUAGE -> GalleryDetailsComponent.GalleryTagType.Language
                    TagType.CATEGORY -> GalleryDetailsComponent.GalleryTagType.Category
                    else -> GalleryDetailsComponent.GalleryTagType.Unknown
                }

                GalleryDetailsComponent.GalleryTag(
                    id = tag.id.toLong(),
                    name = tag.name,
                    type = type
                )
            }

        val related: List<GalleryDetailsComponent.RelatedGallery> = if (!gallery.isLocal) {
            val gal = gallery as Gallery
            if (gal.isRelatedLoaded) {
                gal.related
                    .map {
                        GalleryDetailsComponent.RelatedGallery(
                            id = it.id.toLong(),
                            title = it.title,
                            coverImageUrl = it.thumbnail.toString(),
                            locale = when (it.language) {
                                Language.ENGLISH -> GalleryDetailsComponent.GalleryLocale.English
                                Language.CHINESE -> GalleryDetailsComponent.GalleryLocale.Chinese
                                Language.JAPANESE -> GalleryDetailsComponent.GalleryLocale.Japanese
                                null, Language.UNKNOWN -> GalleryDetailsComponent.GalleryLocale.Unknown(
                                    "unknown"
                                )

                                Language.ALL -> GalleryDetailsComponent.GalleryLocale.Unknown("all")
                            }
                        )
                    }
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }

        val gal = GalleryDetailsComponent.Gallery(
            id = gallery.id.toLong(),
            title = gallery.title,
            tags = GalleryDetailsComponent.GalleryTags(tags),
            pages = pages,
            updated = Instant.fromEpochMilliseconds(gallery.galleryData.uploadDate.time)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
            favoriteCount = gallery.galleryData.favoriteCount,
            isFavorite = Favorites.isFavorite(gallery),
            related = related
        )

        model.update { model ->
            model.copy(
                galleryState = GalleryDetailsComponent.GalleryState.Loaded(gal)
            )
        }
    }

    @Suppress("DEPRECATION")
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

        if (gallery == null) {
            return
        }

        if (favorite) {
            if (gallery is Gallery) {
                Favorites.addFavorite(gallery)
            }
        } else {
            Favorites.removeFavorite(gallery)
        }

        model.update { state ->
            if (state.galleryState is GalleryDetailsComponent.GalleryState.Loaded) {
                state.copy(
                    galleryState = GalleryDetailsComponent.GalleryState.Loaded(
                        state.galleryState.gallery.copy(
                            isFavorite = Favorites.isFavorite(gallery)
                        )
                    )
                )
            } else {
                state
            }
        }
    }

    override fun toggleGridMode() {
        model.update { state ->
            val entries = GalleryDetailsComponent.GridMode.entries
            val newGridMode = if (entries.size > (entries.indexOf(state.gridMode) + 1)) {
                entries[entries.indexOf(state.gridMode) + 1]
            } else {
                GalleryDetailsComponent.GridMode.ONE_COLUMN
            }

            state.copy(gridMode = newGridMode)
        }
    }

    override fun navigateBack() {
        TODO("Not yet implemented")
    }
}
