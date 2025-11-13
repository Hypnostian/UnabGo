package co.edu.unab.sebastianlizcano.unabgo

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val finalUrl = url ?: "https://unab.edu.co/noticias/"

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
                subtitleRes = R.string.announcements
            )

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()
                        loadUrl(finalUrl)
                    }
                }
            )
        }
    }
}
