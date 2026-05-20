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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewDetailScreen(
    navController: NavController,
    url: String,
    title: String
) {
    // Orientación gestionada por el Manifest
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
                    // Configuración mobile-responsive
                    with(settings) {
                        javaScriptEnabled            = true
                        domStorageEnabled            = true
                        useWideViewPort              = true     // respeta <meta viewport>
                        loadWithOverviewMode         = true     // arranca ajustado a la pantalla
                        builtInZoomControls          = true
                        displayZoomControls          = false
                        javaScriptCanOpenWindowsAutomatically = true
                        cacheMode                    = WebSettings.LOAD_DEFAULT
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        layoutAlgorithm  = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                        userAgentString  = userAgentString
                            .replace("; wv", "")
                            .let { ua ->
                                if (ua.contains("Mobile")) ua
                                else "$ua Mobile"
                            }
                    }
                    webViewClient   = WebViewClient()
                    webChromeClient = WebChromeClient()
                    loadUrl(url)
                }
            }
        )
    }
}
