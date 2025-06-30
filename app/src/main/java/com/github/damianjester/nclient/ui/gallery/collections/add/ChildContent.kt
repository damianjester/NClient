package com.github.damianjester.nclient.ui.gallery.collections.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.github.damianjester.nclient.ui.gallery.collections.add.AddToCollectionComponent.Child
import com.github.damianjester.nclient.ui.gallery.collections.add.create.CreateCollectionContent
import com.github.damianjester.nclient.ui.gallery.collections.add.selection.CollectionSelectionContent

@Composable
fun ChildContent(
    component: AddToCollectionComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(fade()),
    ) {
        when (val child = it.instance) {
            is Child.CollectionSelection -> CollectionSelectionContent(child.submittable)
            is Child.CreateCollection -> CreateCollectionContent(child.submittable)
        }
    }
}
