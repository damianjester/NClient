package com.github.damianjester.nclient.ui.gallery.collections.rename

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewRenameCollectionComponent(
    name: String
) : RenameCollectionComponent {
    override val model: Value<RenameCollectionComponent.Model> =
        MutableValue(
            RenameCollectionComponent.Model(
                name = name,
                submittable = true
            )
        )

    override fun onNameChange(name: String) = Unit

    override fun confirm() = Unit

    override fun dismiss() = Unit
}
