package com.github.damianjester.nclient.gallery.search

import com.arkivanov.decompose.ComponentContext

interface GallerySearchComponent {
}

class DefaultGallerySearchComponent(
    componentContext: ComponentContext,
    onGalleryClick: (Long) -> Unit
) : GallerySearchComponent, ComponentContext by componentContext {

}
