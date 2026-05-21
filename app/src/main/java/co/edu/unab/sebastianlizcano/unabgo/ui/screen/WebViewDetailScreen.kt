package co.edu.unab.sebastianlizcano.unabgo.ui.screen

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

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
    "AppleWebKit/537.36 (KHTML, like Gecko) " +
    "Chrome/130.0.0.0 Safari/537.36"

/**
 * JS que oculta y elimina el div #rotate-warning que la web de UNAB
 * muestra cuando detecta viewport pequeño. Triple estrategia:
 * CSS !important + remove del DOM + MutationObserver.
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
        hideWarning();
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
                        userAgentString  = DESKTOP_USER_AGENT
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                            view?.evaluateJavascript(HIDE_ROTATE_WARNING_JS, null)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.evaluateJavascript(HIDE_ROTATE_WARNING_JS, null)
                        }
                    }
                    webChromeClient = WebChromeClient()
                    loadUrl(url)
                }
            }
        )
    }
}
