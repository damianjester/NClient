package com.github.damianjester.nclient.ui.preview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Light", showSystemUi = true)
@Preview(name = "Dark", showSystemUi = true, uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
annotation class PreviewScreen
