package com.github.damianjester.nclient.ui.settings

import com.arkivanov.decompose.ComponentContext

interface SettingsComponent

class DefaultSettingsComponent(
    componentContext: ComponentContext,
) : SettingsComponent, ComponentContext by componentContext
