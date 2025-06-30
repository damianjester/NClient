package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.repo.GalleryCollectionRepository
import com.github.damianjester.nclient.repo.favorite
import com.github.damianjester.nclient.repo.unfavorite
import kotlinx.datetime.Clock

fun interface CollectionFavoriter {
    suspend fun setFavoriteState(id: GalleryId, favorite: Boolean)
}

class DefaultCollectionFavoriter(
    private val repository: GalleryCollectionRepository
) : CollectionFavoriter {
    override suspend fun setFavoriteState(id: GalleryId, favorite: Boolean) {
        val now = Clock.System.now()
        if (favorite) {
            repository.favorite(id, now)
        } else {
            repository.unfavorite(id, now)
        }
    }
}
