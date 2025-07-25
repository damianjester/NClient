package com.github.damianjester.nclient.ui

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.github.damianjester.nclient.ui.RootComponent.Child
import com.github.damianjester.nclient.ui.RootComponent.DialogChild
import com.github.damianjester.nclient.ui.about.AboutRootContent
import com.github.damianjester.nclient.ui.csrf.CsrfTokenRootContent
import com.github.damianjester.nclient.ui.drawer.NClientModalNavigationDrawer
import com.github.damianjester.nclient.ui.gallery.bookmarks.BookmarksRootContent
import com.github.damianjester.nclient.ui.gallery.collections.add.AddToCollectionDialog
import com.github.damianjester.nclient.ui.gallery.collections.create.CreateCollectionDialog
import com.github.damianjester.nclient.ui.gallery.collections.delete.DeleteCollectionDialog
import com.github.damianjester.nclient.ui.gallery.collections.details.CollectionDetailsRootContent
import com.github.damianjester.nclient.ui.gallery.collections.list.GalleryCollectionsRootContent
import com.github.damianjester.nclient.ui.gallery.collections.rename.RenameCollectionDialog
import com.github.damianjester.nclient.ui.gallery.comments.CommentsRootContent
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsRootContent
import com.github.damianjester.nclient.ui.gallery.downloads.DownloadsRootContent
import com.github.damianjester.nclient.ui.gallery.history.HistoryRootContent
import com.github.damianjester.nclient.ui.gallery.history.clear.ClearHistoryDialog
import com.github.damianjester.nclient.ui.gallery.pager.GalleryPagerRootContent
import com.github.damianjester.nclient.ui.gallery.random.RandomGalleryRootContent
import com.github.damianjester.nclient.ui.gallery.search.GallerySearchRootContent
import com.github.damianjester.nclient.ui.settings.SettingsRootContent
import com.github.damianjester.nclient.ui.sort.SortDialog
import kotlinx.coroutines.launch

@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val onDrawerClick: () -> Unit = remember {
        {
            scope.launch {
                drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }
    }

    NClientModalNavigationDrawer(
        component = component.drawer,
        drawerState = drawerState
    ) {
        Children(
            stack = component.stack,
            modifier = modifier,
            animation = stackAnimation(fade()),
        ) {
            when (val child = it.instance) {
                is Child.GallerySearch -> GallerySearchRootContent(child.component, onDrawerClick = onDrawerClick)
                is Child.GalleryDetails -> GalleryDetailsRootContent(child.component)
                is Child.GalleryPager -> GalleryPagerRootContent(child.component)
                is Child.GalleryComments -> CommentsRootContent(child.component)
                is Child.CsrfToken -> CsrfTokenRootContent(child.component)
                is Child.Downloads -> DownloadsRootContent(child.component, onDrawerClick = onDrawerClick)
                is Child.RandomGallery -> RandomGalleryRootContent(child.component, onDrawerClick = onDrawerClick)
                is Child.GalleryCollections -> GalleryCollectionsRootContent(child.component, onDrawerClick = onDrawerClick)
                is Child.CollectionDetails -> CollectionDetailsRootContent(child.component)
                is Child.Bookmarks -> BookmarksRootContent(child.component, onDrawerClick = onDrawerClick)
                is Child.History -> HistoryRootContent(child.component, onDrawerClick = onDrawerClick)
                is Child.Settings -> SettingsRootContent(child.component, onDrawerClick = onDrawerClick)
                is Child.About -> AboutRootContent(child.component, onDrawerClick = onDrawerClick)
            }
        }

        val dialogSlot by component.dialog.subscribeAsState()
        dialogSlot.child?.instance?.also { child ->
            when (child) {
                is DialogChild.AddToCollection -> AddToCollectionDialog(child.component)
                is DialogChild.SortDialog<*> -> SortDialog(child.component)
                is DialogChild.CreateCollection -> CreateCollectionDialog(child.component)
                is DialogChild.RenameCollection -> RenameCollectionDialog(child.component)
                is DialogChild.DeleteCollection -> DeleteCollectionDialog(child.component)
                is DialogChild.ClearHistory -> ClearHistoryDialog(child.component)
            }
        }
    }
}
