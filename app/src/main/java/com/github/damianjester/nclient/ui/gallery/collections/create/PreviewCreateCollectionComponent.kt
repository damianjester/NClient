package com.github.damianjester.nclient.ui.gallery.collections.create

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewCreateCollectionComponent : CreateCollectionComponent {
    override val model: Value<CreateCollectionComponent.Model> =
        MutableValue(
            CreateCollectionComponent.Model(
                name = "Hell, here I come",
                submittable = true
            )
        )

    override fun onNameChanged(value: String) = Unit

    override fun confirm() = Unit

    override fun dismiss() = Unit
}
