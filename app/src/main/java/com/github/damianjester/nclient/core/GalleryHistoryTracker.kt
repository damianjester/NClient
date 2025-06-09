package com.github.damianjester.nclient.core

import com.arkivanov.decompose.router.stack.ChildStack
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.repo.GalleryHistoryRepository
import com.github.damianjester.nclient.ui.DefaultRootComponent
import com.github.damianjester.nclient.ui.RootComponent.Child
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import kotlinx.datetime.Clock

interface GalleryHistoryTracker {
    suspend fun track(id: GalleryId, stack: ChildStack<*, Child>)
}

class DefaultGalleryHistoryTracker(
    private val logger: Logger,
    private val repository: GalleryHistoryRepository,
) : GalleryHistoryTracker {
    override suspend fun track(id: GalleryId, stack: ChildStack<*, Child>) {
        val backstackConfigs = stack.backStack.map { it.configuration }

        // Prevent the gallery being tracked as viewed twice if the user for example navigates
        // from the gallery details component to the gallery pager component
        if (!backstackConfigs.containsDetails(id) && !backstackConfigs.containsPager(id)) {
            logger.i(LogTags.history, "Tracking gallery $id as visited.")
            repository.upsert(id, Clock.System.now())
        }
    }

    private fun List<Any>.containsDetails(id: GalleryId) =
        contains(DefaultRootComponent.Config.GalleryDetails(id))

    private fun List<Any>.containsPager(id: GalleryId) =
        filterIsInstance<DefaultRootComponent.Config.GalleryPager>().firstOrNull { it.id == id } != null
}
