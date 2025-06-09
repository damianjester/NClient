package com.github.damianjester.nclient.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.backStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.GalleryHistoryTracker
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.ui.RootComponent.Child
import com.github.damianjester.nclient.ui.about.AboutComponent
import com.github.damianjester.nclient.ui.about.DefaultAboutComponent
import com.github.damianjester.nclient.ui.csrf.CsrfTokenComponent
import com.github.damianjester.nclient.ui.csrf.DefaultCsrfTokenComponent
import com.github.damianjester.nclient.ui.drawer.DefaultNClientDrawerComponent
import com.github.damianjester.nclient.ui.drawer.NClientDrawerComponent
import com.github.damianjester.nclient.ui.drawer.NClientDrawerItem
import com.github.damianjester.nclient.ui.gallery.bookmarks.BookmarksComponent
import com.github.damianjester.nclient.ui.gallery.bookmarks.DefaultBookmarksComponent
import com.github.damianjester.nclient.ui.gallery.comments.CommentsComponent
import com.github.damianjester.nclient.ui.gallery.comments.DefaultCommentsComponent
import com.github.damianjester.nclient.ui.gallery.details.DefaultGalleryDetailsComponent
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsComponent
import com.github.damianjester.nclient.ui.gallery.downloads.DefaultDownloadsComponent
import com.github.damianjester.nclient.ui.gallery.downloads.DownloadsComponent
import com.github.damianjester.nclient.ui.gallery.favorites.DefaultFavoritesComponent
import com.github.damianjester.nclient.ui.gallery.favorites.FavoritesComponent
import com.github.damianjester.nclient.ui.gallery.history.DefaultHistoryComponent
import com.github.damianjester.nclient.ui.gallery.history.HistoryComponent
import com.github.damianjester.nclient.ui.gallery.history.HistoryTrackerComponent
import com.github.damianjester.nclient.ui.gallery.pager.DefaultGalleryPagerComponent
import com.github.damianjester.nclient.ui.gallery.pager.GalleryPagerComponent
import com.github.damianjester.nclient.ui.gallery.random.DefaultRandomGalleryComponent
import com.github.damianjester.nclient.ui.gallery.random.RandomGalleryComponent
import com.github.damianjester.nclient.ui.gallery.search.DefaultGallerySearchComponent
import com.github.damianjester.nclient.ui.gallery.search.GallerySearchComponent
import com.github.damianjester.nclient.ui.settings.DefaultSettingsComponent
import com.github.damianjester.nclient.ui.settings.SettingsComponent
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    val drawer: NClientDrawerComponent

    sealed class Child {
        sealed class ComponentChild<T> : Child() {
            abstract val component: T
        }

        class GallerySearch(override val component: GallerySearchComponent) : ComponentChild<GallerySearchComponent>()

        class GalleryDetails(override val component: GalleryDetailsComponent) : ComponentChild<GalleryDetailsComponent>()

        class GalleryPager(override val component: GalleryPagerComponent) : ComponentChild<GalleryPagerComponent>()

        class GalleryComments(override val component: CommentsComponent) : ComponentChild<CommentsComponent>()

        class CsrfToken(override val component: CsrfTokenComponent) : ComponentChild<CsrfTokenComponent>()

        class Downloads(override val component: DownloadsComponent) : ComponentChild<DownloadsComponent>()

        class RandomGallery(override val component: RandomGalleryComponent) : ComponentChild<RandomGalleryComponent>()

        class Favorites(override val component: FavoritesComponent) : ComponentChild<FavoritesComponent>()

        class Bookmarks(override val component: BookmarksComponent) : ComponentChild<BookmarksComponent>()

        class History(override val component: HistoryComponent) : ComponentChild<HistoryComponent>()

        class Settings(override val component: SettingsComponent) : ComponentChild<SettingsComponent>()

        class About(override val component: AboutComponent) : ComponentChild<AboutComponent>()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    dispatchers: NClientDispatchers,
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
            Config.Favorites -> Child.Favorites(favoritesComponent(context))
            Config.Bookmarks -> Child.Bookmarks(bookmarksComponent(context))
            Config.History -> Child.History(historyComponent(context))
            is Config.Settings -> Child.Settings(settingsComponent(context))
            is Config.About -> Child.About(aboutComponent(context))
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
                    NClientDrawerItem.Favorites -> Config.Favorites
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
            applicationContext = get(),
            galleryFetcher = get(),
            linkSharer = get(),
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

    private fun favoritesComponent(componentContext: ComponentContext): FavoritesComponent =
        DefaultFavoritesComponent(componentContext)

    private fun bookmarksComponent(componentContext: ComponentContext): BookmarksComponent =
        DefaultBookmarksComponent(componentContext)

    private fun historyComponent(componentContext: ComponentContext): HistoryComponent =
        DefaultHistoryComponent(
            componentContext = componentContext,
            dispatchers = get(),
            onNavigateGallery = { id -> navigation.pushNew(Config.GalleryDetails(id)) },
            historyRepository = get(),
        )

    private fun settingsComponent(componentContext: ComponentContext): SettingsComponent =
        DefaultSettingsComponent(componentContext)

    private fun aboutComponent(componentContext: ComponentContext): AboutComponent =
        DefaultAboutComponent(componentContext)

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
        data object Favorites : Config

        @Serializable
        data object Bookmarks : Config

        @Serializable
        data object History : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data object About : Config
    }
}
