package com.github.damianjester.nclient.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.core.net.toUri
import com.github.damianjester.nclient.R
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface WebPageOpener {
    val intents: Flow<Intent>

    suspend fun open(url: Url)
}

class DefaultWebPageOpener(
    private val context: Context,
) : WebPageOpener {
    private val _intents = MutableSharedFlow<Intent>()
    override val intents: Flow<Intent>
        get() = _intents

    override suspend fun open(url: Url) {
        val defaultBrowserInfo = context.packageManager.resolveDefaultBrowser

        val weblinkIntent = if (defaultBrowserInfo != null) {
            // Default web browser is known, create an explicit intent for that browser
            createBaseIntent(url).apply {
                val packageName = defaultBrowserInfo.activityInfo.packageName
                val activityName = defaultBrowserInfo.activityInfo.name
                component = ComponentName(packageName, activityName)
            }
        } else {
            // Default web browser is unknown, create a chooser intent that allows the user to pick
            // what app should open the web link
            val title = context.getString(R.string.open_in_browser)
            Intent.createChooser(createBaseIntent(url), title, null).apply {
                // Exclude NClient itself from appearing in the chooser menu.
                val excludedComponentNames =
                    arrayOf(ComponentName(context.packageName, "MainActivity"))
                putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponentNames)
            }
        }

        _intents.emit(weblinkIntent)
    }

    private val PackageManager.resolveDefaultBrowser: ResolveInfo?
        get() {
            val browserIntent = Intent(Intent.ACTION_VIEW, "http://".toUri())
            return resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }

    private fun createBaseIntent(url: Url): Intent {
        return Intent(Intent.ACTION_VIEW, url.toString().toUri()).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            } else {
                Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }
}
