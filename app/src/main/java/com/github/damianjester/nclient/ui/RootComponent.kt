package com.github.damianjester.nclient.ui

import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.backStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.GalleryHistoryTracker
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.ui.RootComponent.Child
import com.github.damianjester.nclient.ui.RootComponent.DialogChild
import com.github.damianjester.nclient.ui.RootComponent.DialogChild.AddToCollection
import com.github.damianjester.nclient.ui.RootComponent.DialogChild.DeleteCollection
import com.github.damianjester.nclient.ui.RootComponent.DialogChild.RenameCollection
import com.github.damianjester.nclient.ui.RootComponent.DialogChild.SortDialog
import com.github.damianjester.nclient.ui.about.AboutComponent
import com.github.damianjester.nclient.ui.about.DefaultAboutComponent
import com.github.damianjester.nclient.ui.csrf.CsrfTokenComponent
import com.github.damianjester.nclient.ui.csrf.DefaultCsrfTokenComponent
import com.github.damianjester.nclient.ui.drawer.DefaultNClientDrawerComponent
import com.github.damianjester.nclient.ui.drawer.NClientDrawerComponent
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem
import com.github.damianjester.nclient.ui.gallery.bookmarks.BookmarksComponent
import com.github.damianjester.nclient.ui.gallery.bookmarks.DefaultBookmarksComponent
import com.github.damianjester.nclient.ui.gallery.collections.add.AddToCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.add.DefaultAddToCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.create.CreateCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.create.DefaultCreateCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.delete.DefaultDeleteCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.delete.DeleteCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.details.CollectionDetailsComponent
import com.github.damianjester.nclient.ui.gallery.collections.details.DefaultCollectionDetailsComponent
import com.github.damianjester.nclient.ui.gallery.collections.details.toSortOptions
import com.github.damianjester.nclient.ui.gallery.collections.list.DefaultGalleryCollectionsComponent
import com.github.damianjester.nclient.ui.gallery.collections.list.GalleryCollectionsComponent
import com.github.damianjester.nclient.ui.gallery.collections.list.toSortOptions
import com.github.damianjester.nclient.ui.gallery.collections.rename.DefaultRenameCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.rename.RenameCollectionComponent
import com.github.damianjester.nclient.ui.gallery.comments.CommentsComponent
import com.github.damianjester.nclient.ui.gallery.comments.DefaultCommentsComponent
import com.github.damianjester.nclient.ui.gallery.details.DefaultGalleryDetailsComponent
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsComponent
import com.github.damianjester.nclient.ui.gallery.downloads.DefaultDownloadsComponent
import com.github.damianjester.nclient.ui.gallery.downloads.DownloadsComponent
import com.github.damianjester.nclient.ui.gallery.history.DefaultHistoryComponent
import com.github.damianjester.nclient.ui.gallery.history.HistoryComponent
import com.github.damianjester.nclient.ui.gallery.history.HistoryTrackerComponent
import com.github.damianjester.nclient.ui.gallery.history.toSortOptions
import com.github.damianjester.nclient.ui.gallery.pager.DefaultGalleryPagerComponent
import com.github.damianjester.nclient.ui.gallery.pager.GalleryPagerComponent
import com.github.damianjester.nclient.ui.gallery.random.DefaultRandomGalleryComponent
import com.github.damianjester.nclient.ui.gallery.random.RandomGalleryComponent
import com.github.damianjester.nclient.ui.gallery.search.DefaultGallerySearchComponent
import com.github.damianjester.nclient.ui.gallery.search.GallerySearchComponent
import com.github.damianjester.nclient.ui.settings.DefaultSettingsComponent
import com.github.damianjester.nclient.ui.settings.SettingsComponent
import com.github.damianjester.nclient.ui.sort.DefaultSortDialogComponent
import com.github.damianjester.nclient.ui.sort.SortChangeListener
import com.github.damianjester.nclient.ui.sort.SortDialogComponent
import com.github.damianjester.nclient.ui.sort.SortOption
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import com.github.damianjester.nclient.core.models.CollectionDetailsQuery.SortType as CollectionDetailsSortType
import com.github.damianjester.nclient.core.models.GalleryCollectionQuery.SortType as CollectionsSortType
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery.SortType as HistorySortType

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>
    val dialog: Value<ChildSlot<*, DialogChild>>

    val drawer: NClientDrawerComponent

    sealed class DialogChild {
        class AddToCollection(val component: AddToCollectionComponent) : DialogChild()

        class SortDialog<T>(val component: SortDialogComponent<T>) : DialogChild()

        class CreateCollection(val component: CreateCollectionComponent) : DialogChild()

        class RenameCollection(val component: RenameCollectionComponent) : DialogChild()

        class DeleteCollection(val component: DeleteCollectionComponent) : DialogChild()
    }

    sealed interface Child {
        sealed interface ComponentChild<T> : Child {
            val component: T
        }

        sealed interface SortChangeChild<T> : Child {
            val listener: SortChangeListener<T>
        }

        class GallerySearch(override val component: GallerySearchComponent) : ComponentChild<GallerySearchComponent>

        class GalleryDetails(override val component: GalleryDetailsComponent) : ComponentChild<GalleryDetailsComponent>

        class GalleryPager(override val component: GalleryPagerComponent) : ComponentChild<GalleryPagerComponent>

        class GalleryComments(override val component: CommentsComponent) : ComponentChild<CommentsComponent>

        class CsrfToken(override val component: CsrfTokenComponent) : ComponentChild<CsrfTokenComponent>

        class Downloads(override val component: DownloadsComponent) : ComponentChild<DownloadsComponent>

        class RandomGallery(override val component: RandomGalleryComponent) : ComponentChild<RandomGalleryComponent>

        class GalleryCollections(
            override val component: GalleryCollectionsComponent
        ) : ComponentChild<GalleryCollectionsComponent>, SortChangeChild<CollectionsSortType> {
            override val listener: SortChangeListener<CollectionsSortType>
                get() = component
        }

        class CollectionDetails(
            override val component: CollectionDetailsComponent,
        ) : ComponentChild<CollectionDetailsComponent>, SortChangeChild<CollectionDetailsSortType> {
            override val listener: SortChangeListener<CollectionDetailsSortType>
                get() = component
        }

        class Bookmarks(override val component: BookmarksComponent) : ComponentChild<BookmarksComponent>

        class History(
            override val component: HistoryComponent,
        ) : ComponentChild<HistoryComponent>, SortChangeChild<HistorySortType> {
            override val listener: SortChangeListener<HistorySortType>
                get() = component
        }

        class Settings(override val component: SettingsComponent) : ComponentChild<SettingsComponent>

        class About(override val component: AboutComponent) : ComponentChild<AboutComponent>
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
    private val applicationContext: Context,
    private val initialStack: List<Config> = listOf(Config.GallerySearch),
    private val onFinish: () -> Unit,
    private val galleryHistoryTracker: GalleryHistoryTracker,
) : RootComponent, ComponentContext by componentContext, KoinComponent {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = { initialStack },
            handleBackButton = true,
            childFactory = ::child,
        )

    private val dialogNavigation = SlotNavigation<DialogConfig>()

    override val dialog: Value<ChildSlot<*, DialogChild>> =
        childSlot(
            source = dialogNavigation,
            serializer = DialogConfig.serializer(),
            handleBackButton = true,
            childFactory = ::dialogChild
        )

    init {
        var lastActiveConfig: Config? = null
        stack.subscribe { currentStack ->
            val activeConfig = currentStack.active.configuration

            if (activeConfig != lastActiveConfig) {
                lastActiveConfig = activeConfig as? Config

                val activeInstance = currentStack.active.instance
                if (activeInstance is Child.ComponentChild<*>) {
                    val component = activeInstance.component
                    if (component is HistoryTrackerComponent && !component.trackerStateEntity.state.hasTracked) {
                        component.trackerStateEntity.setAsTracked()
                        coroutineScope.launch {
                            galleryHistoryTracker.track(
                                id = component.trackerStateEntity.state.id,
                                stack = currentStack
                            )
                        }
                    }
                }
            }
        }
    }

    private fun child(config: Config, context: ComponentContext): Child =
        when (config) {
            Config.GallerySearch -> Child.GallerySearch(gallerySearchComponent(context))
            is Config.GalleryDetails -> Child.GalleryDetails(galleryDetailsComponent(context, config))
            is Config.GalleryPager -> Child.GalleryPager(galleryPagerComponent(context, config))
            is Config.GalleryComments -> Child.GalleryComments(galleryCommentsComponent(context, config))
            is Config.CsrfToken -> Child.CsrfToken(csrfTokenComponent(context))
            Config.Downloads -> Child.Downloads(downloadsComponent(context))
            Config.RandomGallery -> Child.RandomGallery(randomGalleryComponent(context))
            Config.Collections -> Child.GalleryCollections(collectionsComponent(context))
            is Config.CollectionDetails -> Child.CollectionDetails(collectionDetailsComponent(context, config))
            Config.Bookmarks -> Child.Bookmarks(bookmarksComponent(context))
            Config.History -> Child.History(historyComponent(context))
            is Config.Settings -> Child.Settings(settingsComponent(context))
            is Config.About -> Child.About(aboutComponent(context))
        }

    private fun dialogChild(config: DialogConfig, context: ComponentContext): DialogChild =
        when (config) {
            is DialogConfig.AddToCollection -> AddToCollection(addToCollectionComponent(context, config.id))
            is DialogConfig.Sort<*> -> SortDialog(sortDialogComponent(context, config))
            is DialogConfig.CreateCollection -> DialogChild.CreateCollection(createCollectionComponent(context))
            is DialogConfig.RenameCollection -> RenameCollection(renameCollectionComponent(context, config))
            is DialogConfig.DeleteCollection -> DeleteCollection(deleteCollectionComponent(context, config))
        }

    override val drawer: NClientDrawerComponent =
        DefaultNClientDrawerComponent(
            componentContext = childContext(key = "Drawer"),
            stack = stack,
            onNavigate = { item ->
                val config = when (item) {
                    NClientDrawerItem.Galleries -> Config.GallerySearch
                    NClientDrawerItem.Downloads -> Config.Downloads
                    NClientDrawerItem.RandomGallery -> Config.RandomGallery
                    NClientDrawerItem.Collections -> Config.Collections
                    NClientDrawerItem.Bookmarks -> Config.Bookmarks
                    NClientDrawerItem.History -> Config.History
                    NClientDrawerItem.Settings -> Config.Settings
                    NClientDrawerItem.About -> Config.About
                }

                navigation.replaceAll(config)
            },
        )

    private fun gallerySearchComponent(componentContext: ComponentContext): GallerySearchComponent =
        DefaultGallerySearchComponent(
            componentContext = componentContext,
            dispatchers = get(),
            pager = get(),
            onNavigateGallery = { id -> navigation.pushNew(Config.GalleryDetails(id)) },
        )

    private fun galleryDetailsComponent(
        componentContext: ComponentContext,
        config: Config.GalleryDetails,
    ): GalleryDetailsComponent =
        DefaultGalleryDetailsComponent(
            componentContext = componentContext,
            dispatchers = get(),
            config = config,
            onNavigatePage = { index ->
                navigation.pushNew(Config.GalleryPager(config.id, index))
            },
            onNavigateComments = {
                navigation.pushNew(Config.GalleryComments(config.id))
            },
            onNavigateRelated = { id ->
                navigation.pushToFront(Config.GalleryDetails(id))
            },
            onNavigateBack = {
                if (stack.backStack.isEmpty()) {
                    onFinish()
                } else {
                    navigation.pop()
                }
            },
            onActivateAddToCollection = {
                dialogNavigation.activate(DialogConfig.AddToCollection(config.id))
            },
            applicationContext = get(),
            galleryFetcher = get(),
            linkSharer = get(),
            collectionRepository = get(),
            favoriter = get()
        )

    private fun galleryPagerComponent(
        componentContext: ComponentContext,
        config: Config.GalleryPager,
    ): GalleryPagerComponent =
        DefaultGalleryPagerComponent(
            componentContext = componentContext,
            dispatchers = get(),
            config = config,
            onNavigateBack = { navigation.pop() },
            pagesFetcher = get(),
            galleryRepository = get(),
            pageSaver = get(),
            pageSharer = get(),
        )

    private fun galleryCommentsComponent(
        componentContext: ComponentContext,
        config: Config.GalleryComments,
    ): CommentsComponent =
        DefaultCommentsComponent(
            componentContext = componentContext,
            dispatchers = get(),
            galleryId = config.id,
            onNavigateBack = { navigation.pop() },
            fetcher = get(),
            webPageOpener = get()
        )

    private fun csrfTokenComponent(
        componentContext: ComponentContext,
    ): CsrfTokenComponent =
        DefaultCsrfTokenComponent(
            componentContext = componentContext
        )

    private fun downloadsComponent(componentContext: ComponentContext): DownloadsComponent =
        DefaultDownloadsComponent(componentContext)

    private fun randomGalleryComponent(componentContext: ComponentContext): RandomGalleryComponent =
        DefaultRandomGalleryComponent(
            componentContext = componentContext,
            onNavigateGallery = { id -> navigation.pushNew(Config.GalleryDetails(id)) },
            dispatchers = get(),
            fetcher = get(),
        )

    private fun collectionsComponent(componentContext: ComponentContext): GalleryCollectionsComponent =
        DefaultGalleryCollectionsComponent(
            componentContext = componentContext,
            onNavigateCollection = { id -> navigation.pushNew(Config.CollectionDetails(id)) },
            onActivateSortDialog = { (type, order) ->

                val config = DialogConfig.Sort(
                    title = applicationContext.getString(R.string.sort_collections),
                    options = CollectionsSortType.entries.toSortOptions(applicationContext),
                    selected = type,
                    order = order
                )

                dialogNavigation.activate(config)
            },
            onActivateCreateCollectionDialog = {
                dialogNavigation.activate(DialogConfig.CreateCollection)
            },
            dispatchers = get(),
            repository = get()
        )

    private fun collectionDetailsComponent(
        componentContext: ComponentContext,
        config: Config.CollectionDetails,
    ): CollectionDetailsComponent =
        DefaultCollectionDetailsComponent(
            componentContext = componentContext,
            config = config,
            onNavigateGallery = { id -> navigation.pushNew(Config.GalleryDetails(id)) },
            onNavigateBack = { navigation.pop() },
            onActivateSortDialog = { sort ->

                val config = DialogConfig.Sort<CollectionDetailsSortType>(
                    title = applicationContext.getString(R.string.sort_galleries),
                    options = CollectionDetailsSortType.entries.toSortOptions(applicationContext),
                    selected = sort.type,
                    order = sort.order
                )

                dialogNavigation.activate(config)
            },
            onActivateRenameDialog = { id, name ->
                dialogNavigation.activate(DialogConfig.RenameCollection(id, name))
            },
            onActivateDeleteDialog = { id, name ->
                dialogNavigation.activate(DialogConfig.DeleteCollection(id, name))
            },
            onActivateAddToCollectionDialog = { id ->
                dialogNavigation.activate(DialogConfig.AddToCollection(id))
            },
            dispatchers = get(),
            repository = get(),
            collectionGalleryRemover = get()
        )

    private fun bookmarksComponent(componentContext: ComponentContext): BookmarksComponent =
        DefaultBookmarksComponent(componentContext)

    private fun historyComponent(componentContext: ComponentContext): HistoryComponent =
        DefaultHistoryComponent(
            componentContext = componentContext,
            dispatchers = get(),
            onNavigateGallery = { id -> navigation.pushNew(Config.GalleryDetails(id)) },
            onActivateSortDialog = { (type, order) ->

                val config = DialogConfig.Sort(
                    title = applicationContext.getString(R.string.sort_history_title),
                    options = HistorySortType.entries.toSortOptions(applicationContext),
                    selected = type,
                    order = order,
                )

                dialogNavigation.activate(config)
            },
            historyRepository = get(),
        )

    private fun settingsComponent(componentContext: ComponentContext): SettingsComponent =
        DefaultSettingsComponent(componentContext)

    private fun aboutComponent(componentContext: ComponentContext): AboutComponent =
        DefaultAboutComponent(componentContext)

    private fun addToCollectionComponent(
        componentContext: ComponentContext,
        id: GalleryId,
    ): AddToCollectionComponent =
        DefaultAddToCollectionComponent(
            context = componentContext,
            galleryId = id,
            onDismissRequest = { dialogNavigation.dismiss() },
            dispatchers = get(),
        )

    @Suppress("UNCHECKED_CAST")
    private fun <T> sortDialogComponent(
        componentContext: ComponentContext,
        config: DialogConfig.Sort<T>,
    ) = DefaultSortDialogComponent(
        componentContext = componentContext,
        config = config,
        onDismissRequest = { dialogNavigation.dismiss() },
        onConfirm = { selected, order ->
            (stack.active.instance as? Child.SortChangeChild<T>)?.listener?.onSortChanged(selected, order)
            dialogNavigation.dismiss()
        }
    )

    private fun createCollectionComponent(componentContext: ComponentContext) =
        DefaultCreateCollectionComponent(
            componentContext = componentContext,
            onDismissRequest = { dialogNavigation.dismiss() },
            dispatchers = get(),
            collectionCreator = get()
        )

    private fun renameCollectionComponent(
        componentContext: ComponentContext,
        config: DialogConfig.RenameCollection,
    ): RenameCollectionComponent = DefaultRenameCollectionComponent(
        componentContext = componentContext,
        config = config,
        onDismissRequest = { dialogNavigation.dismiss() },
        dispatchers = get(),
        collectionRenamer = get()
    )

    fun deleteCollectionComponent(
        componentContext: ComponentContext,
        config: DialogConfig.DeleteCollection,
    ): DeleteCollectionComponent = DefaultDeleteCollectionComponent(
        componentContext = componentContext,
        config = config,
        onCollectionDeleted = {
            if (stack.active.instance is Child.CollectionDetails) {
                navigation.pop()
            }
            dialogNavigation.dismiss()
        },
        onDismissRequest = { dialogNavigation.dismiss() },
        dispatchers = get(),
        deleter = get()
    )

    @Serializable
    sealed interface Config {
        @Serializable
        data object GallerySearch : Config

        @Serializable
        data class GalleryDetails(val id: GalleryId) : Config

        @Serializable
        data class GalleryPager(val id: GalleryId, val pageIndex: Int?) : Config

        @Serializable
        data class GalleryComments(val id: GalleryId) : Config

        @Serializable
        data object CsrfToken : Config

        @Serializable
        data object Downloads : Config

        @Serializable
        data object RandomGallery : Config

        @Serializable
        data object Collections : Config

        @Serializable
        data class CollectionDetails(val id: GalleryCollectionId) : Config

        @Serializable
        data object Bookmarks : Config

        @Serializable
        data object History : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data object About : Config
    }

    @Serializable
    sealed interface DialogConfig {
        @Serializable
        data class AddToCollection(val id: GalleryId) : DialogConfig

        @Serializable
        data class Sort<T>(
            val title: String,
            val options: Set<SortOption<T>>,
            val selected: T,
            val order: SortOrder,
        ) : DialogConfig

        @Serializable
        data object CreateCollection : DialogConfig

        @Serializable
        data class RenameCollection(val id: GalleryCollectionId, val name: String) : DialogConfig

        @Serializable
        data class DeleteCollection(val id: GalleryCollectionId, val name: String) : DialogConfig
    }
}
