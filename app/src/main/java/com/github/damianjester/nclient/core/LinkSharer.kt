package com.github.damianjester.nclient.core

import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface LinkSharer {
    val links: Flow<Url>

    suspend fun share(url: Url)
}

class DefaultLinkSharer : LinkSharer {
    private val _links = MutableSharedFlow<Url>()
    override val links: Flow<Url> = _links

    override suspend fun share(url: Url) {
        _links.emit(url)
    }
}
