package com.github.damianjester.nclient.core

import com.github.damianjester.nclient.core.models.GalleryId

sealed interface NClientError

data class GalleryNotFound(val id: GalleryId) : NClientError
