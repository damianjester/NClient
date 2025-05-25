package com.github.damianjester.nclient.gallery.details;

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.github.damianjester.nclient.api.components.Gallery
import com.github.damianjester.nclient.api.components.GenericGallery
import com.github.damianjester.nclient.gallery.pager.GalleryPagerActivity
import com.github.damianjester.nclient.settings.Global
import com.github.damianjester.nclient.theme.NClientTheme
import com.github.damianjester.nclient.utility.LogUtility

//class GalleryDetailsActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        enableEdgeToEdge(
//            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
//        )
//        super.onCreate(savedInstanceState)
//
//        val gallery = getGalleryFromParcel()
//        val isLocal = intent.getBooleanExtra("$packageName.ISLOCAL", false)
//        val zoom = intent.getIntExtra("$packageName.ZOOM", 0)
//
//        val component = DefaultGalleryDetailsComponent(
//            gallery = gallery,
//            componentContext = defaultComponentContext(),
//            applicationContext = applicationContext
//        )
//
//        setContent {
//            NClientTheme(
//                darkTheme = Global.getTheme() == Global.ThemeScheme.DARK
//            ) {
//                GalleryDetailsScreen(
//                    modifier = Modifier.fillMaxSize(),
//                    component = component,
//                    onBack = { finish() },
//                    onTagClick = { tag -> TODO() },
//                    onPageClick = { page -> startGalleryPagerActivity(gallery, page) },
//                    onRelatedGalleryClick = { gal ->
//                        val related = (gallery as? Gallery)?.related?.firstOrNull { it.id == gal.id.toInt() }
//                        startRelatedGalleryActivity(related)
//                    },
//                    onCopyMetadata = component::copyToClipboard
//                )
//            }
//        }
//    }
//
//    @Suppress("DEPRECATION")
//    private fun getGalleryFromParcel(): GenericGallery? {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            intent.getParcelableExtra("$packageName.GALLERY", GenericGallery::class.java)
//        } else {
//            intent.getParcelableExtra("$packageName.GALLERY");
//        }
//    }
//
//    private fun startGalleryPagerActivity(
//        gallery: GenericGallery?,
//        page: GalleryDetailsComponent.GalleryPage,
//    ) {
//
//        if (gallery == null) {
//            Log.w(LogUtility.LOGTAG, "Couldn't start gallery pager because gallery was null.")
//            return
//        }
//
//        val intent = Intent(this, GalleryPagerActivity::class.java).apply {
//            putExtra("$packageName.GALLERY", gallery)
//            putExtra("$packageName.DIRECTORY", gallery.galleryFolder)
//            putExtra("$packageName.PAGE", page.index + 1)
//        }
//
//        startActivity(intent)
//    }
//
//    private fun startRelatedGalleryActivity(gallery: GenericGallery?) {
//        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
//    }
//
//}
