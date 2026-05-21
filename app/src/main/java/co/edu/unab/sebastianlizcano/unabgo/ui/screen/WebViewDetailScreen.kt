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

/** Mismo JS minimalista que NewsWebScreen: solo CSS, sin tocar el DOM. */
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
                        builtInZoomControls  = false
                        displayZoomControls  = false
                        javaScriptCanOpenWindowsAutomatically = true
                        cacheMode            = WebSettings.LOAD_DEFAULT
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        // User-Agent por defecto (movil): la web entrega la version
                        // responsive correcta. Solo ocultamos el rotate-warning con CSS.
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.evaluateJavascript(HIDE_ROTATE_WARNING_CSS, null)
                        }
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress > 20) {
                                view?.evaluateJavascript(HIDE_ROTATE_WARNING_CSS, null)
                            }
                        }
                    }
                    loadUrl(url)
                }
            }
        )
    }
}
