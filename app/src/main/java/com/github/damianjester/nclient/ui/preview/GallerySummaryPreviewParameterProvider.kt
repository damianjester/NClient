package com.github.damianjester.nclient.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.github.damianjester.nclient.core.models.GallerySummary

class GallerySummaryPreviewParameterProvider : PreviewParameterProvider<List<GallerySummary>> {
    override val values: Sequence<List<GallerySummary>>
        get() = sequenceOf(PreviewData.summaries)
}
