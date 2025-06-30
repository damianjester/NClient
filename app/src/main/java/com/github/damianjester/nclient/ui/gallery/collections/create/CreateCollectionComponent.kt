package com.github.damianjester.nclient.ui.gallery.collections.create

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.github.damianjester.nclient.core.CollectionCreator
import com.github.damianjester.nclient.core.CollectionValidation
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

interface CreateCollectionComponent {
    val model: Value<Model>

    data class Model(
        val name: String = "",
        val submittable: Boolean = false,
    )

    fun onNameChanged(value: String)

    fun confirm()

    fun dismiss()
}

class DefaultCreateCollectionComponent(
    componentContext: ComponentContext,
    private val onDismissRequest: () -> Unit,
    dispatchers: NClientDispatchers,
    private val collectionCreator: CollectionCreator,
) : CreateCollectionComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private val _model = MutableValue(CreateCollectionComponent.Model())
    override val model: Value<CreateCollectionComponent.Model> = _model

    override fun onNameChanged(value: String) {
        _model.update { it.copy(name = value, submittable = value.isSubmittable()) }
    }

    override fun confirm() {
        val (name, submittable) = _model.value
        if (!submittable) {
            return
        }

        coroutineScope.launch {
            collectionCreator.create(name)
            onDismissRequest()
        }
    }

    override fun dismiss() = onDismissRequest()

    private fun String.isSubmittable() = CollectionValidation.validateName(this)
}
