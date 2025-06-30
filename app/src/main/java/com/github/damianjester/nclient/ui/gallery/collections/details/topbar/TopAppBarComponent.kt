package com.github.damianjester.nclient.ui.gallery.collections.details.topbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnResume
import com.github.damianjester.nclient.core.models.GalleryCollectionId
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import com.github.damianjester.nclient.ui.gallery.collections.details.topbar.TopAppBarComponent.Model
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

typealias OnActivateRenameDialog = (id: GalleryCollectionId, name: String) -> Unit
typealias OnActivateDeleteDialog = (id: GalleryCollectionId, name: String) -> Unit

interface TopAppBarComponent {
    val model: Value<Model>

    fun navigateBack()

    fun activateSortDialog()

    fun activateRenameDialog()

    fun activateDeleteDialog()

    data class Model(
        val collectionName: String? = null,
        val isFavorite: Boolean,
    )
}

class DefaultTopAppBarComponent(
    context: ComponentContext,
    private val collectionId: GalleryCollectionId,
    private val onNavigateBack: () -> Unit,
    private val onActivateSortDialog: () -> Unit,
    private val onActivateRenameDialog: OnActivateRenameDialog,
    private val onActivateDeleteDialog: OnActivateDeleteDialog,
    dispatchers: NClientDispatchers,
    repository: GalleryCollectionRepository,
) : TopAppBarComponent, ComponentContext by context {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private val _model = MutableValue(
        Model(
            isFavorite = collectionId.value == GalleryCollectionId.FAVORITES_ID
        )
    )
    override val model: Value<Model> = _model

    init {
        doOnResume {
            coroutineScope.launch {
                repository.collectionName(collectionId)
                    .collect { name -> _model.update { it.copy(collectionName = name) } }
            }
        }
    }

    override fun navigateBack() = onNavigateBack()

    override fun activateSortDialog() = onActivateSortDialog()

    override fun activateRenameDialog() {
        val name = _model.value.collectionName
        if (name != null) {
            onActivateRenameDialog(collectionId, name)
        }
    }

    override fun activateDeleteDialog() {
        val name = _model.value.collectionName
        if (name != null) {
            onActivateDeleteDialog(collectionId, name)
        }
    }
}
