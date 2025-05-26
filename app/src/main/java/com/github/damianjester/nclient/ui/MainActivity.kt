package com.github.damianjester.nclient.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.decompose.defaultComponentContext
import com.github.damianjester.nclient.core.GalleryPageSharer
import com.github.damianjester.nclient.legacy.settings.Global
import com.github.damianjester.nclient.ui.theme.NClientTheme
import com.github.damianjester.nclient.utils.NClientDispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    private val dispatchers by inject<NClientDispatchers>()
    private val sharer by inject<GalleryPageSharer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
//            initialConfig = DefaultRootComponent.Config.CsrfToken
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharer.shares.collect(::startShareActivity)
            }
        }

        setContent {
            NClientTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    RootContent(rootComponent, Modifier.fillMaxSize())
                }
            }
        }
    }

    private suspend fun startShareActivity(share: GalleryPageSharer.GalleryPageShare) =
        withContext(dispatchers.Main) {

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, share.fileUri)
                share.webpageUrl?.let { putExtra(Intent.EXTRA_TEXT, it.toString()) }
                type = share.mineType
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, null))
        }
}
