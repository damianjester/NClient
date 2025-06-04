package com.github.damianjester.nclient.ui.drawer

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnResume
import com.github.damianjester.nclient.ui.DefaultRootComponent.Config
import com.github.damianjester.nclient.ui.RootComponent.Child

enum class NClientDrawerItem {
    Galleries, Downloads, RandomGallery, Favorites, Bookmarks, History, Settings, About
}

interface NClientDrawerComponent {
    val model: Value<Model>

    data class Model(
        val selected: NClientDrawerItem? = null
    )

    fun navigate(item: NClientDrawerItem)
}

class DefaultNClientDrawerComponent(
    componentContext: ComponentContext,
    stack: Value<ChildStack<*, Child>>,
    private val onNavigate: (NClientDrawerItem) -> Unit
) : NClientDrawerComponent, ComponentContext by componentContext {
    private val _model = MutableValue(NClientDrawerComponent.Model())
    override val model: Value<NClientDrawerComponent.Model> = _model

    init {
        doOnResume {
            stack.subscribe {
                (it.active.configuration as? Config).let { config ->
                    val selected = when (config) {
                        Config.GallerySearch -> NClientDrawerItem.Galleries
                        Config.Downloads -> NClientDrawerItem.Downloads
                        Config.RandomGallery -> NClientDrawerItem.RandomGallery
                        Config.Favorites -> NClientDrawerItem.Favorites
                        Config.Bookmarks -> NClientDrawerItem.Bookmarks
                        Config.History -> NClientDrawerItem.History
                        Config.Settings -> NClientDrawerItem.Settings
                        Config.About -> NClientDrawerItem.About
                        else -> null
                    }
                    _model.update { it.copy(selected = selected) }
                }
            }
        }
    }

    override fun navigate(item: NClientDrawerItem) {
        onNavigate(item)
    }
}
