package com.github.damianjester.nclient.db

import app.cash.sqldelight.db.QueryResult
import com.github.damianjester.nclient.CommentEntity
import com.github.damianjester.nclient.CommentEntityQueries
import com.github.damianjester.nclient.CommentPosterEntity
import com.github.damianjester.nclient.GalleryDetailsEntity
import com.github.damianjester.nclient.GalleryPageEntityQueries
import com.github.damianjester.nclient.GalleryQueries
import com.github.damianjester.nclient.GallerySummaryEntity
import com.github.damianjester.nclient.TagEntity
import com.github.damianjester.nclient.TagEntityQueries
import com.github.damianjester.nclient.core.GalleryId

inline fun QueryResult<Long>.orUpdate(block: () -> QueryResult<Long>): QueryResult<Long> {
    return if (value == 0L) {
        // No rows were affected because of INSERT OR IGNORE therefore run UPDATE in block
        block()
    } else {
        this
    }
}

fun GalleryPageEntityQueries.selectPagesWithMediaIdForGallery(id: GalleryId) =
    selectPagesForGallery(id.value) {
        galleryId: Long,
        pageIndex: Long,
        fileExtension: String,
        width: Long,
        height: Long,
        mediaId: Long,
        ->
        GalleryPageWithMediaId(
            galleryId = galleryId,
            pageIndex = pageIndex,
            fileExtension = fileExtension,
            width = width,
            height = height,
            mediaId = mediaId
        )
    }

fun CommentEntityQueries.selectCommentsWithPosterEntity(id: GalleryId) =
    selectCommentsForGallery(id.value) { commentId, posterId, date, body, createdAt, username, avatarPath ->
        CommentEntityWithPosterEntity(
            comment = CommentEntity(
                id = commentId,
                galleryId = id.value,
                posterId = posterId,
                date = date,
                body = body,
                createdAt = createdAt
            ),
            poster = CommentPosterEntity(
                id = posterId,
                username = username,
                avatarPath = avatarPath
            )
        )
    }

fun GalleryQueries.updateSummary(entity: GallerySummaryEntity): QueryResult<Long> {
    return updateSummary(
        mediaId = entity.mediaId,
        prettyTitle = entity.prettyTitle,
        coverThumbnailFileExtension = entity.coverThumbnailFileExtension,
        id = entity.id // WHERE
    )
}

fun GalleryQueries.updateDetails(entity: GalleryDetailsEntity): QueryResult<Long> {
    return updateDetails(
        coverFileExtension = entity.coverFileExtension,
        numFavorites = entity.numFavorites,
        englishTitle = entity.englishTitle,
        japaneseTitle = entity.japaneseTitle,
        uploadDate = entity.uploadDate,
        updatedAt = entity.updatedAt,
        galleryId = entity.galleryId // WHERE
    )
}

fun TagEntityQueries.updateTag(entity: TagEntity): QueryResult<Long> {
    return updateTag(
        type = entity.type,
        name = entity.name,
        count = entity.count,
        urlPath = entity.urlPath,
        id = entity.id // WHERE
    )
}
