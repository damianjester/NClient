package com.github.damianjester.nclient.ui.gallery.random

import com.arkivanov.decompose.ComponentContext

interface RandomGalleryComponent

class DefaultRandomGalleryComponent(
    componentContext: ComponentContext,
) : RandomGalleryComponent, ComponentContext by componentContext
