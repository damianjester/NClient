package com.github.damianjester.nclient.ui.gallery.favorites

import com.arkivanov.decompose.ComponentContext

interface FavoritesComponent

class DefaultFavoritesComponent(
    componentContext: ComponentContext,
) : FavoritesComponent, ComponentContext by componentContext
