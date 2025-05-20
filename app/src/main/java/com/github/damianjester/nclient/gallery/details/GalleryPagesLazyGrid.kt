package com.github.damianjester.nclient.gallery.details

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

@Composable
fun GalleryPagesLazyGrid(
    modifier: Modifier = Modifier,
    gallery: GalleryDetailsComponent.Gallery,
    gridMode: GalleryDetailsComponent.GridMode,
    onPageClick: (GalleryDetailsComponent.GalleryPage) -> Unit,
    onRelatedGalleryClick: (GalleryDetailsComponent.RelatedGallery) -> Unit,
    onTagClick: (GalleryDetailsComponent.GalleryTag) -> Unit,
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
                tags = gallery.tags,
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
                        dayOfMonth(Padding.NONE);
                        char(' ')
                        monthName(MonthNames.ENGLISH_ABBREVIATED);
                        char(' ')
                        year()
                        chars(" at ")
                        hour()
                        char(':')
                        minute()
                    }
                }

                Text("Uploaded on ${formatter.format(gallery.updated)}")
                Text("${gallery.pages.size} pages")
                Text("${gallery.favoriteCount} favorites")
            }
        }

        items(gallery.pages, key = { it.index }) { page ->
            GalleryPage(
                modifier = Modifier.fillMaxSize(),
                page = page,
                onClick = remember(page.index) { { onPageClick(page) } }
            )
        }

        item(key = "related", span = { GridItemSpan(gridMode.count) }) {
            RelatedGalleriesSection(
                modifier = Modifier.fillMaxWidth(),
                galleries = gallery.related,
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
    galleryId: Long,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit,
) {
    Row(modifier = modifier.padding(bottom = 8.dp)) {
        Text(
            "ID:",
            style = MaterialTheme.typography.h6
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "$galleryId",
            modifier = Modifier
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onLongClick = onLongClick,
                    onClick = {}
                ),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Normal
        )
    }
}

val tagTypes = setOf(
    GalleryDetailsComponent.GalleryTagType.Parody,
    GalleryDetailsComponent.GalleryTagType.General,
    GalleryDetailsComponent.GalleryTagType.Artist,
    GalleryDetailsComponent.GalleryTagType.Group,
    GalleryDetailsComponent.GalleryTagType.Language,
    GalleryDetailsComponent.GalleryTagType.Category,
)

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun GalleryTags(
    modifier: Modifier,
    tags: GalleryDetailsComponent.GalleryTags,
    onTagClick: (GalleryDetailsComponent.GalleryTag) -> Unit,
    onTagLongClick: (GalleryDetailsComponent.GalleryTag) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {

        for (type in tagTypes) {

            val heading = when (type) {
                GalleryDetailsComponent.GalleryTagType.Parody -> "Parody"
                GalleryDetailsComponent.GalleryTagType.Character -> "Character"
                GalleryDetailsComponent.GalleryTagType.General -> "Tags"
                GalleryDetailsComponent.GalleryTagType.Artist -> "Artist"
                GalleryDetailsComponent.GalleryTagType.Group -> "Group"
                GalleryDetailsComponent.GalleryTagType.Language -> "Language"
                GalleryDetailsComponent.GalleryTagType.Category -> "Category"
                else -> continue
            } + ":"

            val sectionTags = when (type) {
                GalleryDetailsComponent.GalleryTagType.Parody -> tags.parody
                GalleryDetailsComponent.GalleryTagType.Character -> tags.character
                GalleryDetailsComponent.GalleryTagType.General -> tags.general
                GalleryDetailsComponent.GalleryTagType.Artist -> tags.artist
                GalleryDetailsComponent.GalleryTagType.Group -> tags.group
                GalleryDetailsComponent.GalleryTagType.Language -> tags.language
                GalleryDetailsComponent.GalleryTagType.Category -> tags.category
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
                    style = MaterialTheme.typography.h6
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
    page: GalleryDetailsComponent.GalleryPage,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3.toFloat() / 4)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .clickable { onClick() },
    ) {

        val imageModel: Any = when (val image = page.image) {
            is GalleryDetailsComponent.GalleryPageImage.Local -> image.file
            is GalleryDetailsComponent.GalleryPageImage.Remote -> {
                // TODO: Or original if grid has one cell
                image.thumbnailUrl
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
            color = MaterialTheme.colors.surface
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
