package com.github.damianjester.nclient.db.models

import com.github.damianjester.nclient.CommentEntity
import com.github.damianjester.nclient.CommentPosterEntity

data class CommentEntityWithPosterEntity(
    val comment: CommentEntity,
    val poster: CommentPosterEntity,
)
