package com.github.damianjester.nclient.csrf

import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.damianjester.nclient.settings.Global
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CsrfTokenRootContent(
    component: CsrfTokenComponent,
    modifier: Modifier = Modifier,
) {

    CsrfTokenWebview(
        modifier = modifier,
        onCsrfTokenFound = component::onCsrfTokenFound
    )
}


@Composable
fun CsrfTokenWebview(
    modifier: Modifier = Modifier,
    onCsrfTokenFound: (String) -> Unit,
) {
    val url = "https://nhentai.net"

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        coroutineScope.launch {
            val cookieManager = CookieManager.getInstance()
            var csrfCookieFound = false

            do {
                Log.wtf("token", "checking for CSRF token")
                val token = cookieManager.getCookie(url)
                    ?.split("; ")
                    ?.map { it.split("=", limit = 2) }
                    ?.associate { it.first() to it.lastOrNull() }
                    ?.firstNotNullOfOrNull { (key, value) ->
                        if (key == "csrftoken" && value != null) value else null
                    }
                    ?.let { token ->
                        onCsrfTokenFound(token)
                        csrfCookieFound = true
                    }

                if (!csrfCookieFound) {
                    Log.wtf("token", "CSRF token not found...")
                }

                delay(100)
            } while (!csrfCookieFound)
        }
    }

    AndroidView(
        factory = {
            WebView(it).apply webview@{
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = WebViewClient()
//                setWebChromeClient(WebChromeClient())
                CookieManager.getInstance().apply {
                    setAcceptCookie(true)
                    setAcceptThirdPartyCookies(this@webview, true)
                }

                getSettings().apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    setUserAgentString(Global.getUserAgent())
                    allowContentAccess = true
                }

                loadUrl(url)
            }
        },
        modifier = modifier,
        update = {
            it.loadUrl(url)
        })
}
