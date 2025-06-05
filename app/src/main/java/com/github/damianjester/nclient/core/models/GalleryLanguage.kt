package com.github.damianjester.nclient.core.models

import kotlinx.serialization.Serializable

@Serializable
sealed interface GalleryLanguage {
    @Serializable
    sealed interface KnownLanguage : GalleryLanguage {
        val id: GalleryTagId
    }

    @Serializable
    data object English : KnownLanguage {
        override val id = GalleryTagId(12227L)
    }

    @Serializable
    data object Chinese : KnownLanguage {
        override val id = GalleryTagId(29963L)
    }

    @Serializable
    data object Japanese : KnownLanguage {
        override val id = GalleryTagId(6346L)
    }

    @Serializable
    data object Unknown : GalleryLanguage

    companion object {
        fun fromLongTagId(ids: List<Long>): GalleryLanguage {
            return fromTagId(ids.map { GalleryTagId(it) })
        }

        fun fromTagId(ids: List<GalleryTagId>): GalleryLanguage {
            return when {
                ids.contains(Japanese.id) -> Japanese
                ids.contains(English.id) -> English
                ids.contains(Chinese.id) -> Chinese
                else -> Unknown
            }
        }
    }
}
