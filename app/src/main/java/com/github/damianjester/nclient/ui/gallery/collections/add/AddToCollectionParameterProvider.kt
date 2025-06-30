package com.github.damianjester.nclient.ui.gallery.collections.add

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.github.damianjester.nclient.ui.preview.PreviewData

class AddToCollectionParameterProvider : PreviewParameterProvider<AddToCollectionComponent> {
    override val values: Sequence<AddToCollectionComponent>
        get() = sequenceOf(
            PreviewAddToCollectionComponent(PreviewAddToCollectionComponent.Mode.Selection(PreviewData.collections)),
            PreviewAddToCollectionComponent(PreviewAddToCollectionComponent.Mode.Create("Read Later")),
        )
}
