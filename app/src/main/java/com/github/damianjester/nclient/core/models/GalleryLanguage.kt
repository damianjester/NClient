package com.github.damianjester.nclient.core.models

sealed interface GalleryLanguage {
    interface KnownLanguage {
        val id: GalleryTagId
    }

    data object English : GalleryLanguage, KnownLanguage {
        override val id = GalleryTagId(12227L)
    }

    data object Chinese : GalleryLanguage, KnownLanguage {
        override val id = GalleryTagId(29963L)
    }

    data object Japanese : GalleryLanguage, KnownLanguage {
        override val id = GalleryTagId(6346L)
    }

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
