package com.github.damianjester.nclient.ui.gallery.collections.add.create

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PreviewCreateCollectionComponent(name: String) : CreateCollectionComponent {
    override val submittable: Flow<Boolean> = flowOf(true)

    override val model: Value<CreateCollectionComponent.Model> =
        MutableValue(
            CreateCollectionComponent.Model(
                name = name,
                submittable = true
            )
        )

    override fun onNewCollectionNameChanged(value: String) = Unit

    override fun onDone() = Unit

    override fun submit() = Unit
}
