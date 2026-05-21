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

// User-Agent de escritorio. Engana al sitio web de UNAB para que NO muestre
// el mensaje "Gira tu telefono para ver el contenido" (que era el bug que el
// usuario veia: el overlay no era del SO, sino de la propia pagina UNAB).
private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
    "AppleWebKit/537.36 (KHTML, like Gecko) " +
    "Chrome/130.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsWebScreen(
    navController: NavController?,
    url: String?
) {
    val finalUrl = url?.takeIf { it.isNotBlank() } ?: "https://unab.edu.co/noticias/"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F024C))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            HeaderBar(
                navController = navController,
                subtitleRes   = R.string.announcements
            )

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory  = { ctx ->
                    WebView(ctx).apply {
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
                            // User-Agent de escritorio para evitar el mensaje
                            // "Gira tu telefono" que la web de UNAB muestra
                            // cuando detecta dispositivo movil.
                            userAgentString = DESKTOP_USER_AGENT
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
