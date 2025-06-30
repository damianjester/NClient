package com.github.damianjester.nclient.ui.gallery.collections.delete

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.CollectionDeleter
import com.github.damianjester.nclient.ui.DefaultRootComponent.DialogConfig
import com.github.damianjester.nclient.ui.gallery.collections.delete.DeleteCollectionComponent.Model
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

interface DeleteCollectionComponent {
    val model: Value<Model>

    data class Model(
        val name: String,
    )

    fun confirm()

    fun dismiss()
}

class DefaultDeleteCollectionComponent(
    componentContext: ComponentContext,
    private val config: DialogConfig.DeleteCollection,
    private val onCollectionDeleted: () -> Unit,
    private val onDismissRequest: () -> Unit,
    dispatchers: NClientDispatchers,
    private val deleter: CollectionDeleter,
) : DeleteCollectionComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private val _model = MutableValue(Model(config.name))
    override val model: Value<Model> = _model

    override fun confirm() {
        coroutineScope.launch {
            deleter.delete(config.id)
            onCollectionDeleted()
        }
    }

    override fun dismiss() = onDismissRequest()
}
