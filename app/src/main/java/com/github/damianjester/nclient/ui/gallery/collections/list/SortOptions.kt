package com.github.damianjester.nclient.ui.gallery.collections.list

import android.content.Context
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GalleryCollectionQuery.SortType
import com.github.damianjester.nclient.ui.sort.SortOption
import kotlin.enums.EnumEntries

fun EnumEntries<SortType>.toSortOptions(context: Context): Set<SortOption<SortType>> = mapNotNull { type ->
    when (type) {
        SortType.UpdatedAt -> SortOption(
            label = context.getString(R.string.collections_sort_last_updated),
            type = type
        )

        SortType.Name -> SortOption(
            label = context.getString(R.string.collections_sort_name),
            type = type
        )

        SortType.Size -> SortOption(
            label = context.getString(R.string.collections_sort_size),
            type = type
        )

        SortType.ID -> null
    }
}.toSet()
