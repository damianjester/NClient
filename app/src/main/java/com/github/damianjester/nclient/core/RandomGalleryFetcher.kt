package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.Gallery
import com.github.damianjester.nclient.net.NHentaiHttpClient
import com.github.damianjester.nclient.repo.GalleryRepository

interface RandomGalleryFetcher {
    suspend fun fetch(): Gallery
}

class DefaultRandomGalleryFetcher(
    private val client: NHentaiHttpClient,
    private val repository: GalleryRepository,
) : RandomGalleryFetcher {
    override suspend fun fetch(): Gallery {
        val response = client.getRandomGalleryDetails()
        repository.upsertGalleryDetails(response)
        return repository.selectGalleryDetails(response.gallery.id).gallery
    }
}
