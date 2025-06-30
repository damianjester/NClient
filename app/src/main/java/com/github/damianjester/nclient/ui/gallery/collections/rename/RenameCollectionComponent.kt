package com.github.damianjester.nclient.ui.gallery.collections.rename

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.github.damianjester.nclient.core.CollectionRenamer
import com.github.damianjester.nclient.core.CollectionValidation
import com.github.damianjester.nclient.ui.DefaultRootComponent
import com.github.damianjester.nclient.ui.gallery.collections.rename.RenameCollectionComponent.Model
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.launch

interface RenameCollectionComponent {
    val model: Value<Model>

    fun onNameChange(name: String)

    fun confirm()

    fun dismiss()

    data class Model(
        val name: String,
        val submittable: Boolean,
    )
}

class DefaultRenameCollectionComponent(
    componentContext: ComponentContext,
    private val config: DefaultRootComponent.DialogConfig.RenameCollection,
    private val onDismissRequest: () -> Unit,
    dispatchers: NClientDispatchers,
    private val collectionRenamer: CollectionRenamer,
) : RenameCollectionComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate)

    private val _model = MutableValue(Model(config.name, config.name.isSubmittable()))
    override val model: Value<Model> = _model

    override fun onNameChange(name: String) {
        _model.update { it.copy(name = name, submittable = name.isSubmittable()) }
    }

    override fun confirm() {
        val (name, submittable) = _model.value
        if (!submittable) {
            return
        }

        coroutineScope.launch {
            collectionRenamer.rename(config.id, name)
            onDismissRequest()
        }
    }

    override fun dismiss() = onDismissRequest()

    private fun String.isSubmittable() =
        this != config.name && CollectionValidation.validateName(this)
}
