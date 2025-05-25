package com.github.damianjester.nclient

import java.io.File
import kotlin.math.abs
import kotlin.math.log10

val File.fileExtension: String?
    get() = name.split(".").lastOrNull()

fun GalleryPage.filenameForExternalStorage(
    id: GalleryId,
    pageCount: Int,
    fileExtension: String,
): String {
    // Formats the page number with zero prefixes based on the total amount of pages, for example:
    // If there are 51 pages than the prefix will be have the following format: 01, 02, 03, ..., 50, 51
    // Or if there are 150 pages than the prefixes will be 001, 002, 003, ..., 061, ... 149, 150
    // And with 1000+ pages it will be 0003, 0051, 0148, 1028 etc.
    val paddedPageNumber = (index + 1).toString().padStart(pageCount.length, '0')
    return "${id.value}_$paddedPageNumber.$fileExtension"
}

private val Int.length
    get() = when (this) {
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }
