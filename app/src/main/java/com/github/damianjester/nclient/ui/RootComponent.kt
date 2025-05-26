package com.github.damianjester.nclient.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.ui.RootComponent.Child
import com.github.damianjester.nclient.ui.csrf.CsrfTokenComponent
import com.github.damianjester.nclient.ui.csrf.DefaultCsrfTokenComponent
import com.github.damianjester.nclient.ui.gallery.comments.CommentsComponent
import com.github.damianjester.nclient.ui.gallery.comments.DefaultCommentsComponent
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsComponent
import com.github.damianjester.nclient.ui.gallery.details.DefaultGalleryDetailsComponent
import com.github.damianjester.nclient.ui.gallery.pager.GalleryPagerComponent
import com.github.damianjester.nclient.ui.gallery.pager.NewGalleryPagerComponent
import com.github.damianjester.nclient.ui.gallery.search.DefaultGallerySearchComponent
import com.github.damianjester.nclient.ui.gallery.search.GallerySearchComponent
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class GallerySearch(val component: GallerySearchComponent) : Child()

        class GalleryDetails(val component: GalleryDetailsComponent) : Child()

        class GalleryPager(val component: GalleryPagerComponent) : Child()

        class GalleryComments(val component: CommentsComponent) : Child()

        class CsrfToken(val component: CsrfTokenComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    initialConfig: Config = Config.GallerySearch,
) : RootComponent, ComponentContext by componentContext, KoinComponent {
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = initialConfig,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, context: ComponentContext): Child =
        when (config) {
            Config.GallerySearch -> Child.GallerySearch(gallerySearchComponent(context))
            is Config.GalleryDetails -> Child.GalleryDetails(
                galleryDetailsComponent(context, config)
            )

            is Config.GalleryPager -> Child.GalleryPager(
                galleryPagerComponent(context, config)
            )

            is Config.GalleryComments -> Child.GalleryComments(
                galleryCommentsComponent(context, config)
            )

            is Config.CsrfToken -> Child.CsrfToken(
                csrfTokenComponent(context)
            )
        }

    private fun gallerySearchComponent(componentContext: ComponentContext): GallerySearchComponent =
        DefaultGallerySearchComponent(
            componentContext = componentContext,
            pager = get(),
            fetcher = get(),
            onNavigateGallery = { id -> navigation.pushNew(Config.GalleryDetails(id)) },
        )

    private fun galleryDetailsComponent(
        componentContext: ComponentContext,
        config: Config.GalleryDetails,
    ): GalleryDetailsComponent =
        DefaultGalleryDetailsComponent(
            componentContext = componentContext,
            applicationContext = get(),
            config = config,
            dispatchers = get(),
            galleryLoader = get(),
            galleryObserver = get(),
            pagesFetcher = get(),
            onNavigateBack = { navigation.pop() },
            onNavigatePage = { index ->
                navigation.pushNew(Config.GalleryPager(config.id, index))
            },
            onNavigateComments = {
                navigation.pushNew(Config.GalleryComments(config.id))
            }
            tagsObserver = get()
        )

    private fun galleryPagerComponent(
        componentContext: ComponentContext,
        config: Config.GalleryPager,
    ): GalleryPagerComponent =
        NewGalleryPagerComponent(
            componentContext = componentContext,
            config = config,
            onNavigateBack = { navigation.pop() },
            detailsObserver = get(),
            pagesFetcher = get(),
            pageSaver = get(),
            pageSharer = get(),
        )

    private fun galleryCommentsComponent(
        componentContext: ComponentContext,
        config: Config.GalleryComments,
    ): CommentsComponent =
        DefaultCommentsComponent(
            componentContext = componentContext,
            galleryId = config.id,
            onNavigateBack = { navigation.pop() },
            fetcher = get(),
            observer = get(),
        )

    private fun csrfTokenComponent(
        componentContext: ComponentContext,
    ): CsrfTokenComponent =
        DefaultCsrfTokenComponent(
            componentContext = componentContext
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
    }
}
