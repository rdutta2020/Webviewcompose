@file:Suppress("SetJavaScriptEnabled")

package com.example.webviewcompose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    var progress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        }
    }

    // Cleanup when this composable leaves composition
    DisposableEffect(webView) {
        onDispose {
            try {
                webView.stopLoading()
                // If webChromeClient is nullable, clear it:
                webView.webChromeClient = null

                // If webViewClient is non-nullable, replace it with a no-op instance
                webView.webViewClient = object : WebViewClient() {}

                webView.clearHistory()
                webView.removeAllViews()
                webView.pauseTimers()
                webView.destroy()
            } catch (t: Throwable) {
                // swallow any errors during cleanup (log if needed)
            }
        }
    }


    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            LinearProgressIndicator(
                progress = progress / 100f, // fix: pass Float, not lambda
                modifier = Modifier.fillMaxWidth()
            )
        }

        /**
         * AndroidView is the Compose integration point for embedding a classic Android View
         */
        AndroidView(
            /** Factory is called to create the view instance the first time.
             You pass your webView and call loadUrl(url) to start loading.
             (Because you used remember earlier, factory will receive the remembered WebView.)
            */
            factory = {
                webView.apply { loadUrl(url) }
            },
            /**
                Modifier.fillMaxSize() makes the view fill the parent
             */
            modifier = Modifier.fillMaxSize(),
            /**
             * Update is called on recomposition to update the existing view.
             */
            update = { view ->
                // handles page navigation & loading events
                view.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        isLoading = true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        isLoading = false
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        isLoading = false
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest,
                    ): WebResourceResponse? {
                        // SAML parsing logic can go here — careful with thread/context
                        return null
                    }
                }
                //handles browser features (progress, JS dialogs, favicons, title, etc.)
                view.webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        progress = newProgress
                    }
                }
            }
        )
    }
}

