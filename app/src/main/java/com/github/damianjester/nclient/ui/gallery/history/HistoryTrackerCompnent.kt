package com.github.damianjester.nclient.ui.gallery.history

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.github.damianjester.nclient.core.models.GalleryId
import kotlinx.serialization.Serializable

typealias HistoryTrackerState = HistoryTrackerStateEntity.State

interface HistoryTrackerComponent : ComponentContext {
    val trackerStateEntity: HistoryTrackerStateEntity
}

class HistoryTrackerStateEntity : InstanceKeeper.Instance {
    var state: State
        private set

    constructor(id: GalleryId) {
        state = State(id)
    }

    constructor(savedState: State) {
        state = savedState
    }

    fun setAsTracked() {
        state = state.copy(hasTracked = true)
    }

    @Serializable
    data class State(val id: GalleryId, val hasTracked: Boolean = false)
}
