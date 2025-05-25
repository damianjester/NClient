package com.github.damianjester.nclient.ui.gallery.comments

import android.content.Context
import androidx.annotation.PluralsRes
import com.github.damianjester.nclient.R
import kotlin.time.Duration

private const val SECONDS_FOR_JUST_NOW = 10
private const val SECONDS_IN_MINUTE = 60
private const val MINUTES_IN_HOUR = 60
private const val HOURS_IN_DAY = 24
private const val DAYS_IN_WEEK = 7
private const val DAYS_IN_MONTH = 30
private const val DAYS_IN_YEAR = 365

fun formatCommentPostDate(
    context: Context,
    duration: Duration,
): String {

    if (duration.inWholeSeconds < SECONDS_FOR_JUST_NOW) {
        return context.getString(R.string.just_now)
    }

    val formattedDuration = when {
        duration.inWholeSeconds < SECONDS_IN_MINUTE -> context.getString(
            R.string.seconds,
            duration.inWholeSeconds
        )

        duration.inWholeMinutes < MINUTES_IN_HOUR -> {
            context.resources
                .getQuantityString(
                    R.plurals.minute,
                    duration.inWholeMinutes.toInt(),
                    duration.inWholeMinutes
                )
        }

        duration.inWholeHours < HOURS_IN_DAY -> {
            formatPrimarySecondaryDuration(
                context = context,
                primaryTime = duration.inWholeHours,
                primaryPluralRes = R.plurals.hour,
                secondaryTime = duration.inWholeMinutes % MINUTES_IN_HOUR,
                secondaryPluralRes = R.plurals.minute
            )
        }

        duration.inWholeDays < DAYS_IN_WEEK -> {
            formatPrimarySecondaryDuration(
                context = context,
                primaryTime = duration.inWholeDays,
                primaryPluralRes = R.plurals.day,
                secondaryTime = duration.inWholeHours % HOURS_IN_DAY,
                secondaryPluralRes = R.plurals.hour
            )
        }

        duration.inWholeDays < DAYS_IN_MONTH -> {
            formatPrimarySecondaryDuration(
                context = context,
                primaryTime = duration.inWholeDays / DAYS_IN_WEEK,
                primaryPluralRes = R.plurals.week,
                secondaryTime = duration.inWholeDays % DAYS_IN_WEEK,
                secondaryPluralRes = R.plurals.day
            )
        }

        duration.inWholeDays < 365 -> {
            formatPrimarySecondaryDuration(
                context = context,
                primaryTime = duration.inWholeDays / DAYS_IN_MONTH,
                primaryPluralRes = R.plurals.month,
                secondaryTime = duration.inWholeDays % DAYS_IN_MONTH / DAYS_IN_WEEK,
                secondaryPluralRes = R.plurals.week
            )
        }

        else -> {
            formatPrimarySecondaryDuration(
                context = context,
                primaryTime = duration.inWholeDays / DAYS_IN_YEAR,
                primaryPluralRes = R.plurals.year,
                secondaryTime = duration.inWholeDays / DAYS_IN_YEAR / DAYS_IN_MONTH,
                secondaryPluralRes = R.plurals.month
            )
        }
    }

    return context.getString(R.string.posted_ago, formattedDuration)
}

private fun formatSecondaryTimeUnit(
    context: Context,
    @PluralsRes plural: Int,
    block: () -> Long,
): String = block()
    .takeIf { it > 0 }
    ?.let {
        val singularOrPlural = context.resources.getQuantityString(plural, it.toInt(), it)
        ", $singularOrPlural"
    }
    ?: ""

private fun formatPrimarySecondaryDuration(
    context: Context,
    primaryTime: Long,
    @PluralsRes primaryPluralRes: Int,
    secondaryTime: Long,
    @PluralsRes secondaryPluralRes: Int,
): String {
    val primaryText =
        context.resources.getQuantityString(primaryPluralRes, primaryTime.toInt(), primaryTime)
    val secondaryText = formatSecondaryTimeUnit(context, secondaryPluralRes) { secondaryTime }
    return "$primaryText$secondaryText"
}
