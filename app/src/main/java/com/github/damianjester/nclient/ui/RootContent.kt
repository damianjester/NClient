package com.github.damianjester.nclient.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.github.damianjester.nclient.ui.RootComponent.Child
import com.github.damianjester.nclient.ui.csrf.CsrfTokenRootContent
import com.github.damianjester.nclient.ui.gallery.comments.GalleryCommentsRootContent
import com.github.damianjester.nclient.ui.gallery.details.GalleryDetailsRootContent
import com.github.damianjester.nclient.ui.gallery.pager.GalleryPagerRootContent
import com.github.damianjester.nclient.ui.gallery.search.GallerySearchRootContent

@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(fade()),
    ) {
        when (val child = it.instance) {
            is Child.GallerySearch -> GallerySearchRootContent(child.component)
            is Child.GalleryDetails -> GalleryDetailsRootContent(child.component)
            is Child.GalleryPager -> GalleryPagerRootContent(child.component)
            is Child.GalleryComments -> GalleryCommentsRootContent(child.component)
            is Child.CsrfToken -> CsrfTokenRootContent(child.component)
        }
    }
}
