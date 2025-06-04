package com.github.damianjester.nclient.ui.about

import com.arkivanov.decompose.ComponentContext

interface AboutComponent

class DefaultAboutComponent(
    componentContext: ComponentContext,
) : AboutComponent, ComponentContext by componentContext
