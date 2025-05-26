package com.github.damianjester.nclient.ui.gallery.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.damianjester.nclient.core.Gallery
import com.github.damianjester.nclient.core.GalleryId
import com.github.damianjester.nclient.core.GalleryPage
import com.github.damianjester.nclient.core.GalleryPageImages
import com.github.damianjester.nclient.core.GalleryTag
import com.github.damianjester.nclient.core.GalleryTagType
import com.github.damianjester.nclient.core.RelatedGallery
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

@Composable
fun GalleryPagesLazyGrid(
    modifier: Modifier = Modifier,
    gallery: Gallery,
    pages: List<GalleryPage>,
    tags: GalleryDetailsComponent.GalleryTags,
    related: List<RelatedGallery>,
    gridMode: GalleryDetailsComponent.GridMode,
    onPageClick: (GalleryPage) -> Unit,
    onRelatedGalleryClick: (RelatedGallery) -> Unit,
    onTagClick: (GalleryTag) -> Unit,
    onCopyMetadata: (GalleryDetailsComponent.MetadataCopy) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridMode.count),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "id", span = { GridItemSpan(gridMode.count) }) {
            GalleryId(
                gallery.id,
                onLongClick = {
                    onCopyMetadata(GalleryDetailsComponent.MetadataCopy.Id(gallery.id))
                }
            )
        }

        item(key = "tags", span = { GridItemSpan(gridMode.count) }) {
            GalleryTags(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                tags = tags,
                onTagClick = onTagClick,
                onTagLongClick = {
                    onCopyMetadata(GalleryDetailsComponent.MetadataCopy.Tag(it))
                }
            )
        }

        item(key = "metadata", span = { GridItemSpan(gridMode.count) }) {
            Column(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                val formatter = remember {
                    LocalDateTime.Format {
                        dayOfMonth(Padding.NONE)
                        char(' ')
                        monthName(MonthNames.ENGLISH_ABBREVIATED)
                        char(' ')
                        year()
                        chars(" at ")
                        hour()
                        char(':')
                        minute()
                    }
                }

                Text("Uploaded on ${formatter.format(gallery.updated)}")
                Text("${pages.size} pages")
                Text("${gallery.favoriteCount} favorites")
            }
        }

        items(pages, key = { it.index }) { page ->
            GalleryPage(
                modifier = Modifier.fillMaxSize(),
                page = page,
                showHighRes = gridMode == GalleryDetailsComponent.GridMode.ONE_COLUMN,
                onClick = remember(page.index) { { onPageClick(page) } }
            )
        }

        item(key = "related", span = { GridItemSpan(gridMode.count) }) {
            RelatedGalleriesSection(
                modifier = Modifier.fillMaxWidth(),
                galleries = related,
                onGalleryClick = onRelatedGalleryClick,
            )
        }

        item(key = "bottom spacer", span = { GridItemSpan(gridMode.count) }) {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun GalleryId(
    id: GalleryId,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit,
) {
    Row(modifier = modifier.padding(bottom = 8.dp)) {
        Text(
            "ID:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "${id.value}",
            modifier = Modifier
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onLongClick = onLongClick,
                    onClick = {}
                ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal
        )
    }
}

val tagTypes = setOf(
    GalleryTagType.Parody,
    GalleryTagType.General,
    GalleryTagType.Artist,
    GalleryTagType.Group,
    GalleryTagType.Language,
    GalleryTagType.Category,
)

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun GalleryTags(
    modifier: Modifier,
    tags: GalleryDetailsComponent.GalleryTags,
    onTagClick: (GalleryTag) -> Unit,
    onTagLongClick: (GalleryTag) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        for (type in tagTypes) {
            val heading = when (type) {
                GalleryTagType.Parody -> "Parody"
                GalleryTagType.Character -> "Character"
                GalleryTagType.General -> "Tags"
                GalleryTagType.Artist -> "Artist"
                GalleryTagType.Group -> "Group"
                GalleryTagType.Language -> "Language"
                GalleryTagType.Category -> "Category"
                else -> continue
            } + ":"

            val sectionTags = when (type) {
                GalleryTagType.Parody -> tags.parody
                GalleryTagType.Character -> tags.character
                GalleryTagType.General -> tags.general
                GalleryTagType.Artist -> tags.artist
                GalleryTagType.Group -> tags.group
                GalleryTagType.Language -> tags.language
                GalleryTagType.Category -> tags.category
                else -> continue
            }

            if (sectionTags.isEmpty()) {
                continue
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    heading,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.titleMedium
                )
                sectionTags.forEach { tag ->
                    GalleryTag(
                        tag.name,
                        onLongClick = { onTagLongClick(tag) },
                        onClick = { onTagClick(tag) }
                    )
                }
            }
        }
    }
}

@Composable
fun GalleryPage(
    modifier: Modifier = Modifier,
    page: GalleryPage,
    showHighRes: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.toFloat() / 4)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() },
    ) {
        val imageModel: Any = when (val image = page.image) {
            is GalleryPageImages.Local -> {
                if (showHighRes) {
                    image.localOriginal
                } else {
                    image.localThumbnail
                }
            }
            is GalleryPageImages.Remote -> {
                if (showHighRes) {
                    image.remoteOriginal.url.toString()
                } else {
                    image.remoteThumbnail.url.toString()
                }
            }
        }

        AsyncImage(
            model = imageModel,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart),
            color = MaterialTheme.colorScheme.surface
                .copy(alpha = 0.5f),
            shape = RoundedCornerShape(
                bottomEnd = 4.dp,
            )
        ) {
            Text(
                "${page.index + 1}",
                modifier = Modifier.padding(4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
