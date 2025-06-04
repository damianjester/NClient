package com.github.damianjester.nclient.ui.gallery.history

import com.arkivanov.decompose.ComponentContext

interface HistoryComponent

class DefaultHistoryComponent(
    componentContext: ComponentContext,
) : HistoryComponent, ComponentContext by componentContext
