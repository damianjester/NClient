package com.github.damianjester.nclient.ui.sort

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.models.SortOrder

class PreviewSortDialogComponent(
    title: String = "Sort something",
    options: Set<SortOption<String>>,
    selected: String
) : SortDialogComponent<String> {
    override val model: Value<SortDialogComponent.Model<String>> =
        MutableValue(
            SortDialogComponent.Model(
                title = title,
                options = options,
                selected = selected,
            )
        )

    override fun select(item: String) = Unit

    override fun order(order: SortOrder) = Unit

    override fun confirm() = Unit

    override fun dismiss() = Unit
}
