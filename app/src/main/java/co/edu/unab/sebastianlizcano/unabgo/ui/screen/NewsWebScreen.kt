package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R

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
fun NewsWebScreen(
    navController: NavController?,
    url: String?
) {
    val finalUrl = url?.takeIf { it.isNotBlank() } ?: "https://unab.edu.co/noticias/"
    // Orientación portrait gestionada por el Manifest (android:screenOrientation="portrait")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            HeaderBar(
                navController = navController,
                subtitleRes   = R.string.announcements
            )

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory  = { ctx ->
                    WebView(ctx).apply {
                        // Configuración mobile-responsive
                        with(settings) {
                            javaScriptEnabled            = true
                            domStorageEnabled            = true
                            useWideViewPort              = true     // respeta <meta viewport>
                            loadWithOverviewMode         = true     // arranca ajustado a la pantalla
                            builtInZoomControls          = true
                            displayZoomControls          = false    // sin botones +/- visibles
                            javaScriptCanOpenWindowsAutomatically = true
                            cacheMode                    = WebSettings.LOAD_DEFAULT
                            mediaPlaybackRequiresUserGesture = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            // Forzar layout estilo móvil
                            layoutAlgorithm  = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                            // User-Agent móvil para que WordPress entregue el sitio responsive
                            userAgentString  = userAgentString
                                .replace("; wv", "")
                                .let { ua ->
                                    if (ua.contains("Mobile")) ua
                                    else "$ua Mobile"
                                }
                        }
                        webViewClient   = WebViewClient()
                        webChromeClient = WebChromeClient()
                        loadUrl(finalUrl)
                    }
                }
            )
        }
    }
}

// LockOrientationPortrait y findActivity ahora viven en utils/OrientationUtils.kt
