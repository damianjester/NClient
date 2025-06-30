package com.github.damianjester.nclient.ui.sort

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.github.damianjester.nclient.core.models.SortOrder
import com.github.damianjester.nclient.ui.DefaultRootComponent
import com.github.damianjester.nclient.ui.sort.SortDialogComponent.Model

interface SortDialogComponent<T> {
    val model: Value<Model<T>>

    fun select(item: T)

    fun order(order: SortOrder)

    fun confirm()

    fun dismiss()

    data class Model<T>(
        val title: String,
        val options: Set<SortOption<T>>,
        val selected: T,
        val order: SortOrder = SortOrder.DESC,
    )
}

class DefaultSortDialogComponent<T>(
    componentContext: ComponentContext,
    config: DefaultRootComponent.DialogConfig.Sort<T>,
    private val onDismissRequest: () -> Unit,
    private val onConfirm: (selected: T, order: SortOrder) -> Unit,
) : SortDialogComponent<T>, ComponentContext by componentContext {
    private val _model = MutableValue(
        Model(
            title = config.title,
            options = config.options,
            selected = config.selected,
            order = config.order
        )
    )
    override val model: Value<Model<T>> = _model

    override fun select(item: T) {
        _model.update { it.copy(selected = item) }
    }

    override fun order(order: SortOrder) {
        _model.update { it.copy(order = order) }
    }

    override fun confirm() {
        val model = _model.value
        onConfirm(model.selected, model.order)
    }

    override fun dismiss() = onDismissRequest()
}
