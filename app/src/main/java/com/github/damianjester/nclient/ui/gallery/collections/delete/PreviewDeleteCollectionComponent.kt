package com.github.damianjester.nclient.ui.gallery.collections.delete

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewDeleteCollectionComponent : DeleteCollectionComponent {
    override val model: Value<DeleteCollectionComponent.Model> =
        MutableValue(
            DeleteCollectionComponent.Model(
                name = "Slop"
            )
        )

    override fun confirm() = Unit

    override fun dismiss() = Unit
}
