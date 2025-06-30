package com.github.damianjester.nclient.ui.gallery.collections.details

import android.content.Context
import com.github.damianjester.nclient.R
import com.github.damianjester.nclient.core.models.CollectionDetailsQuery.SortType
import com.github.damianjester.nclient.ui.sort.SortOption
import kotlin.enums.EnumEntries

fun EnumEntries<SortType>.toSortOptions(context: Context): Set<SortOption<SortType>> = map { type ->
    when (type) {
        SortType.Added -> SortOption(
            label = context.getString(R.string.sort_option_last_added),
            type = type
        )

        SortType.Title -> SortOption(
            label = context.getString(R.string.sort_option_title),
            type = type
        )

        SortType.ID -> SortOption(
            label = context.getString(R.string.sort_option_upload_date),
            type = type
        )
    }
}.toSet()
