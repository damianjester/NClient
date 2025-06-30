package com.github.damianjester.nclient.ui.gallery.collections.add

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.models.GalleryCollection
import com.github.damianjester.nclient.ui.gallery.collections.add.create.PreviewCreateCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.add.selection.PreviewCollectionSelectionComponent

class PreviewAddToCollectionComponent(
    private val mode: Mode,
) : AddToCollectionComponent {
    private val selectionComponent = PreviewCollectionSelectionComponent((mode as? Mode.Selection)?.collections ?: emptyList())

    private val createComponent = PreviewCreateCollectionComponent((mode as? Mode.Create)?.name ?: "")

    override val stack: Value<ChildStack<*, AddToCollectionComponent.Child>> =
        MutableValue(ChildStack(Unit, child()))

    private fun child(): AddToCollectionComponent.Child =
        when (mode) {
            is Mode.Selection -> AddToCollectionComponent.Child.CollectionSelection(selectionComponent)
            is Mode.Create -> AddToCollectionComponent.Child.CreateCollection(createComponent)
        }

    override val submittable: Value<Boolean> = MutableValue(true)

    override fun submit() = Unit

    override fun navigateBack() = Unit

    sealed interface Mode {
        data class Selection(val collections: List<GalleryCollection>) : Mode

        data class Create(val name: String) : Mode
    }
}
