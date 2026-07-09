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

/**
 * JS minimalista y seguro: SOLO inyecta CSS para ocultar el #rotate-warning.
 *  - NO toca el DOM (no remove, no MutationObserver) -> no rompe la pagina.
 *  - Selectores especificos -> NO matchea otros elementos.
 *  - Idempotente -> se puede inyectar varias veces sin problemas.
 */
private const val HIDE_ROTATE_WARNING_CSS = """
javascript:(function(){
  if (document.getElementById('unab-go-hide-rotate')) return;
  var s = document.createElement('style');
  s.id = 'unab-go-hide-rotate';
  s.innerHTML = '#rotate-warning, .rotate-warning { display: none !important; visibility: hidden !important; opacity: 0 !important; pointer-events: none !important; }';
  (document.head || document.documentElement).appendChild(s);
})();
"""

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
                            builtInZoomControls  = false      // sin controles de zoom flotantes
                            displayZoomControls  = false
                            javaScriptCanOpenWindowsAutomatically = true
                            cacheMode            = WebSettings.LOAD_DEFAULT
                            mediaPlaybackRequiresUserGesture = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            // Mantenemos el User-Agent movil por defecto. La web de UNAB
                            // entrega la version mobile-responsive con el UA por defecto,
                            // que se ve mucho mejor que la version desktop en el celular.
                            // El JS inyectado ocultara solo el rotate-warning.
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Ejecuta el JS DESPUES de que la pagina termina de cargar
                                view?.evaluateJavascript(HIDE_ROTATE_WARNING_CSS, null)
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                // Inyectar tambien a medida que carga - asi el rotate-warning
                                // se oculta lo antes posible sin esperar a que termine todo.
                                if (newProgress > 20) {
                                    view?.evaluateJavascript(HIDE_ROTATE_WARNING_CSS, null)
                                }
                            }
                        }
                        loadUrl(finalUrl)
                    }
                }
            )
        }
    }
}
