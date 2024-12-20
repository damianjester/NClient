package com.github.damianjester.nclient.gallery.comments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.github.damianjester.nclient.settings.Global
import com.github.damianjester.nclient.theme.NClientTheme

private const val EXTRA_GALLERYID = "GALLERYID"

class CommentsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val galleryId = intent.getIntExtra("$packageName.$EXTRA_GALLERYID", -1)
        val component = DefaultCommentsComponent(
            componentContext = defaultComponentContext(),
            galleryId = galleryId.toLong()
        )

        setContent {
            NClientTheme(
                darkTheme = Global.getTheme() == Global.ThemeScheme.DARK
            ) {
                CommentsScreen(
                    modifier = Modifier.fillMaxSize(),
                    component = component,
                    onBack = { finish() }
                )
            }
        }
    }

}
