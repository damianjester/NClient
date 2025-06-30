package com.github.damianjester.nclient.ui.gallery.history.clear

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.github.damianjester.nclient.repo.GalleryHistoryRepository
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface ClearHistoryComponent {
    fun confirm()

    fun dismiss()
}

class DefaultClearHistoryComponent(
    componentContext: ComponentContext,
    private val onDismissRequest: () -> Unit,
    private val repository: GalleryHistoryRepository,
) : ClearHistoryComponent, KoinComponent, ComponentContext by componentContext {
    private val instance = instanceKeeper.getOrCreate { Instance(get()) }

    override fun confirm() {
        instance.coroutineScope.launch {
            repository.deleteAll()
            onDismissRequest()
        }
    }

    override fun dismiss() {
        onDismissRequest()
    }

    private class Instance(
        dispatchers: NClientDispatchers,
    ) : InstanceKeeper.Instance {
        val coroutineScope = CoroutineScope(dispatchers.Main.immediate + SupervisorJob())

        override fun onDestroy() {
            super.onDestroy()
            coroutineScope.cancel()
        }
    }
}
