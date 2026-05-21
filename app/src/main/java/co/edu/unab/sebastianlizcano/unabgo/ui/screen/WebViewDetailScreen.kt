package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import co.edu.unab.sebastianlizcano.unabgo.ui.components.HeaderBar

// User-Agent de escritorio (Chrome Windows) — evita que la web de UNAB
// muestre el aviso "Gira tu telefono" que dispara cuando detecta movil.
private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
    "AppleWebKit/537.36 (KHTML, like Gecko) " +
    "Chrome/130.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewDetailScreen(
    navController: NavController,
    url: String,
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        HeaderBar(navController = navController)

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory  = { context ->
                WebView(context).apply {
                    with(settings) {
                        javaScriptEnabled    = true
                        domStorageEnabled    = true
                        useWideViewPort      = true
                        loadWithOverviewMode = true
                        builtInZoomControls  = true
                        displayZoomControls  = false
                        javaScriptCanOpenWindowsAutomatically = true
                        cacheMode            = WebSettings.LOAD_DEFAULT
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        userAgentString = DESKTOP_USER_AGENT
                    }
                    webViewClient   = WebViewClient()
                    webChromeClient = WebChromeClient()
                    loadUrl(url)
                }
            }
        )
    }
}
