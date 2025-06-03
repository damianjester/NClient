package com.github.damianjester.nclient.core

sealed interface NClientError

data class GalleryNotFound(val id: GalleryId) : NClientError
