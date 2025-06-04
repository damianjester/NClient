package com.github.damianjester.nclient.ui.gallery.downloads

import com.arkivanov.decompose.ComponentContext

interface DownloadsComponent

class DefaultDownloadsComponent(
    componentContext: ComponentContext,
) : DownloadsComponent, ComponentContext by componentContext
