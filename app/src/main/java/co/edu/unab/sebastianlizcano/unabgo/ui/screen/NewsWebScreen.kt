package co.edu.unab.sebastianlizcano.unabgo.ui.screen

import co.edu.unab.sebastianlizcano.unabgo.R

import android.annotation.SuppressLint
import android.graphics.Bitmap
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

// User-Agent de escritorio (refuerzo por si la web tambien lo evalua)
private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
    "AppleWebKit/537.36 (KHTML, like Gecko) " +
    "Chrome/130.0.0.0 Safari/537.36"

/**
 * JavaScript que se inyecta para ocultar y eliminar el div <rotate-warning>
 * que la web de UNAB muestra cuando detecta un viewport pequeño.
 *
 * Estrategia triple:
 *   1. Inyecta CSS con !important para impedir que se muestre.
 *   2. Busca el div #rotate-warning y lo elimina del DOM.
 *   3. Usa un MutationObserver para que si la web lo vuelve a inyectar
 *      via JavaScript, se elimine de nuevo automaticamente.
 */
private val HIDE_ROTATE_WARNING_JS = """
    (function() {
        function hideWarning() {
            var warnings = document.querySelectorAll(
                '#rotate-warning, .rotate-warning, [class*="rotate-warning"], [id*="rotate-warning"]'
            );
            warnings.forEach(function(el) {
                el.style.display = 'none';
                el.style.visibility = 'hidden';
                if (el.parentNode) el.parentNode.removeChild(el);
            });
        }

        // 1. Inyectar CSS permanente (con !important para ganar contra el de la web)
        if (!document.getElementById('unab-go-hide-rotate')) {
            var s = document.createElement('style');
            s.id = 'unab-go-hide-rotate';
            s.innerHTML =
                '#rotate-warning, .rotate-warning, [class*="rotate-warning"] {' +
                '  display: none !important;' +
                '  visibility: hidden !important;' +
                '  opacity: 0 !important;' +
                '  height: 0 !important;' +
                '  width: 0 !important;' +
                '  position: absolute !important;' +
                '  left: -9999px !important;' +
                '}';
            (document.head || document.documentElement).appendChild(s);
        }

        // 2. Eliminar elementos existentes
        hideWarning();

        // 3. Observar cambios al DOM por si la web re-inyecta el aviso
        if (document.body) {
            new MutationObserver(hideWarning).observe(
                document.body,
                { childList: true, subtree: true }
            );
        }
    })();
""".trimIndent()

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
                            userAgentString  = DESKTOP_USER_AGENT
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                super.onPageStarted(view, url, favicon)
                                // Inyectar JS lo antes posible (incluso antes del DOMContentLoaded)
                                view?.evaluateJavascript(HIDE_ROTATE_WARNING_JS, null)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Reinyectar JS al terminar la carga por si la inyeccion
                                // temprana no funciono
                                view?.evaluateJavascript(HIDE_ROTATE_WARNING_JS, null)
                            }
                        }
                        webChromeClient = WebChromeClient()
                        loadUrl(finalUrl)
                    }
                }
            )
        }
    }
}
