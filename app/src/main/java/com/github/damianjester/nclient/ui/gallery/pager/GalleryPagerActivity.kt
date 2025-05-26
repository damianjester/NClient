package com.github.damianjester.nclient.ui.gallery.pager

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.github.damianjester.nclient.legacy.api.components.GenericGallery
import com.github.damianjester.nclient.legacy.settings.Global
import com.github.damianjester.nclient.ui.theme.NClientTheme

class GalleryPagerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        val gallery = getGalleryFromParcel()
        val page = intent.extras?.getInt("$packageName.PAGE", 1)?.minus(1)

        val component = DefaultGalleryPagerComponent(
            componentContext = defaultComponentContext(),
            genericGallery = gallery,
            context = this
        )

        setContent {
            NClientTheme(
                darkTheme = Global.getTheme() == Global.ThemeScheme.DARK
            ) {
                GalleryPagerScreen(
                    modifier = Modifier.fillMaxSize(),
                    component = component,
                    initialPage = page ?: 0,
                    onBack = { finish() },
                    onDownloadPage = component::savePageToGallery,
                    onSharePage = { page, includeUrl -> component.sharePage(page, includeUrl) },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    @Suppress("DEPRECATION")
    private fun getGalleryFromParcel(): GenericGallery? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("$packageName.GALLERY", GenericGallery::class.java)
        } else {
            intent.getParcelableExtra("$packageName.GALLERY")
        }
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
