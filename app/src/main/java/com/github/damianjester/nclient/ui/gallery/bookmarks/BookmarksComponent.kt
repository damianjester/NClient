package com.github.damianjester.nclient.ui.gallery.bookmarks

import com.arkivanov.decompose.ComponentContext

interface BookmarksComponent

class DefaultBookmarksComponent(
    componentContext: ComponentContext,
) : BookmarksComponent, ComponentContext by componentContext
