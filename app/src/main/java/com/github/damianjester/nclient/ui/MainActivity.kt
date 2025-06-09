package com.github.damianjester.nclient.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.decompose.defaultComponentContext
import com.github.damianjester.nclient.core.GalleryPageSharer
import com.github.damianjester.nclient.core.LinkSharer
import com.github.damianjester.nclient.core.ScreenCaffeinater
import com.github.damianjester.nclient.core.WebPageOpener
import com.github.damianjester.nclient.ui.theme.NClientTheme
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.logger.LogTags
import com.github.damianjester.nclient.utils.logger.Logger
import com.github.damianjester.nclient.utils.logger.e
import com.github.damianjester.nclient.utils.logger.i
import io.ktor.http.Url
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private val dispatchers by inject<NClientDispatchers>()
    private val sharer by inject<GalleryPageSharer>()
    private val webPageOpener by inject<WebPageOpener>()
    private val screenCaffeinater by inject<ScreenCaffeinater>()
    private val deepLinker by inject<DeepLinker>()
    private val linkSharer by inject<LinkSharer>()
    private val logger by inject<Logger>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            dispatchers = dispatchers,
            initialStack = deepLinker.link(intent),
            onFinish = ::finish,
            galleryHistoryTracker = get(),
        )

        collectFlows()

        setContent {
            NClientTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    RootContent(rootComponent, Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        logger.i(LogTags.caffeine, "Clearing window FLAG_KEEP_SCREEN_ON flag (onPause).")
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharer.shares.collect(::startShareActivity)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                webPageOpener.intents.collect(::startWebpageIntent)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                screenCaffeinater.keepScreenOn.collect(::setKeepScreenOnFlag)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                linkSharer.links.collect(::shareLink)
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

    private fun setKeepScreenOnFlag(keepOn: Boolean) {
        if (keepOn) {
            logger.i(LogTags.caffeine, "Adding window FLAG_KEEP_SCREEN_ON flag.")
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            logger.i(LogTags.caffeine, "Clearing window FLAG_KEEP_SCREEN_ON flag.")
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun startWebpageIntent(weblinkIntent: Intent) {
        try {
            logger.i("Starting web page intent (data = ${weblinkIntent.data}).")
            startActivity(weblinkIntent)
        } catch (ex: ActivityNotFoundException) {
            logger.e("Unable to start web page intent (data = ${weblinkIntent.data}).", ex)
        }
    }

    private suspend fun shareLink(url: Url) {
        withContext(dispatchers.Main) {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url.toString())
                type = "text/plain"
            }

            startActivity(Intent.createChooser(shareIntent, null))
        }
    }
}
