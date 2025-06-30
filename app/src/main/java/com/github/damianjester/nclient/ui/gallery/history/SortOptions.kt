package com.github.damianjester.nclient.ui.gallery.history

import android.content.Context
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.GalleryHistoryQuery.SortType
import com.github.damianjester.nclient.ui.sort.SortOption
import kotlin.enums.EnumEntries

fun EnumEntries<SortType>.toSortOptions(context: Context): Set<SortOption<SortType>> = map { type ->
    when (type) {
        SortType.ViewCount ->
            SortOption(
                label = context.getString(R.string.sort_type_views),
                type = SortType.ViewCount
            )

        SortType.LastVisit ->
            SortOption(
                label = context.getString(R.string.sort_type_date),
                type = SortType.LastVisit
            )
    }
}.toSet()
