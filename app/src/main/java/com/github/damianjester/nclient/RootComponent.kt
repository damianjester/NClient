package com.github.damianjester.nclient

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.RootComponent.Child
import com.github.damianjester.nclient.gallery.comments.CommentsComponent
import com.github.damianjester.nclient.gallery.comments.DefaultCommentsComponent
import com.github.damianjester.nclient.gallery.details.GalleryDetailsComponent
import com.github.damianjester.nclient.gallery.details.NewGalleryDetailsComponent
import com.github.damianjester.nclient.gallery.pager.GalleryPagerComponent
import com.github.damianjester.nclient.gallery.pager.NewGalleryPagerComponent
import com.github.damianjester.nclient.gallery.search.DefaultGallerySearchComponent
import com.github.damianjester.nclient.gallery.search.GallerySearchComponent
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class GallerySearch(val component: GallerySearchComponent) : Child()
        class GalleryDetails(val component: GalleryDetailsComponent) : Child()
        class GalleryPager(val component: GalleryPagerComponent) : Child()
        class GalleryComments(val component: CommentsComponent) : Child()
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
        }

    private fun gallerySearchComponent(componentContext: ComponentContext): GallerySearchComponent =
        DefaultGallerySearchComponent(
            componentContext = componentContext,
            onGalleryClick = { id -> navigation.pushNew(Config.GalleryDetails(id)) },
        )

    private fun galleryDetailsComponent(
        componentContext: ComponentContext,
        config: Config.GalleryDetails,
    ): GalleryDetailsComponent =
        NewGalleryDetailsComponent(
            componentContext = componentContext,
            config = config,
            onNavigateBack = { navigation.pop() },
            onPageClick = { id, index ->
                navigation.pushNew(Config.GalleryPager(id, index))
            }
        )

    private fun galleryPagerComponent(
        componentContext: ComponentContext,
        config: Config.GalleryPager,
    ): GalleryPagerComponent =
        NewGalleryPagerComponent(
            componentContext = componentContext,
            config = config,
            onNavigateBack = { navigation.pop() }
        )

    private fun galleryCommentsComponent(
        componentContext: ComponentContext,
        config: Config.GalleryComments,
    ): CommentsComponent =
        DefaultCommentsComponent(
            componentContext = componentContext,
            galleryId = config.galleryId,
            onNavigateBack = { navigation.pop() }
        )

    @Serializable
    sealed interface Config {

        @Serializable
        data object GallerySearch : Config

        @Serializable
        data class GalleryDetails(val galleryId: Long) : Config

        @Serializable
        data class GalleryPager(val galleryId: Long, val pageIndex: Int?) : Config

        @Serializable
        data class GalleryComments(val galleryId: Long) : Config
    }
}
