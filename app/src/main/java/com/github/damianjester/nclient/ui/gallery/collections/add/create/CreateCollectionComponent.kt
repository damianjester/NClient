package com.github.damianjester.nclient.ui.gallery.collections.add.create

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.github.damianjester.nclient.core.CollectionCreator
import com.github.damianjester.nclient.core.CollectionValidation
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.ui.gallery.collections.add.AddToCollectionComponent
import com.github.damianjester.nclient.ui.utils.asStateFlow
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface CreateCollectionComponent : AddToCollectionComponent.Child.Submittable {
    val model: Value<Model>

    fun onNewCollectionNameChanged(value: String)

    fun onDone()

    data class Model(
        val name: String = "",
        val submittable: Boolean = false,
    )
}

class DefaultCreateCollectionComponent(
    componentContext: ComponentContext,
    private val onNewCollectionCreated: (id: GalleryCollectionId) -> Unit,
    dispatchers: NClientDispatchers,
    private val collectionCreator: CollectionCreator,
) : CreateCollectionComponent, ComponentContext by componentContext {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private val _model = MutableValue(CreateCollectionComponent.Model())
    override val model: Value<CreateCollectionComponent.Model> = _model

    override val submittable: Flow<Boolean> = _model.asStateFlow().map { it.submittable }

    override fun onNewCollectionNameChanged(value: String) {
        _model.update { it.copy(name = value, submittable = CollectionValidation.validateName(value)) }
    }

    override fun submit() {
        createCollection()
    }

    override fun onDone() {
        createCollection()
    }

    private fun createCollection() {
        val (name, submittable) = _model.value
        if (!submittable) {
            return
        }

        coroutineScope.launch {
            val id = collectionCreator.create(name)
            onNewCollectionCreated(id)
        }
    }
}
