package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val dataStore = LanguageDataStore(newBase)
        val savedLang = runBlocking {
            dataStore.getLanguage().first() ?: "es"
        }
        val context = LocaleManager.loadLocale(newBase, savedLang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Se ejecuta dentro de un Composable
            val windowSizeClass = calculateWindowSizeClass(activity = this)

            // Configurar escala y dimensiones según el tamaño
            val (fontScale, dimens) = when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> 0.92f to AppDimens(
                    titleXL = 38f,
                    titleL = 20f,
                    body = 13f,
                    buttonHeight = 50,
                    logoSize = 76,
                    heroImageSize = 190,
                    gapS = 6,
                    gapM = 12,
                    gapL = 24
                )
                WindowWidthSizeClass.Medium -> 1.0f to AppDimens(
                    titleXL = 45f,
                    titleL = 22f,
                    body = 14f,
                    buttonHeight = 55,
                    logoSize = 90,
                    heroImageSize = 210,
                    gapS = 8,
                    gapM = 16,
                    gapL = 32
                )
                else -> 1.12f to AppDimens(
                    titleXL = 52f,
                    titleL = 24f,
                    body = 16f,
                    buttonHeight = 60,
                    logoSize = 110,
                    heroImageSize = 240,
                    gapS = 10,
                    gapM = 20,
                    gapL = 40
                )
            }

            val navController = rememberNavController()
            val context = this
            val dataStore = remember { LanguageDataStore(context) }

            var currentLang by remember { mutableStateOf(LocaleManager.getCurrentLanguage(context)) }

            LaunchedEffect(Unit) {
                dataStore.getLanguage().collect { lang ->
                    if (lang != null && lang != currentLang) {
                        currentLang = lang
                        LocaleManager.setLocale(context, lang)
                    }
                }
            }

            key(currentLang) {
                CompositionLocalProvider(
                    LocalDensity provides Density(LocalDensity.current.density, fontScale),
                    LocalAppDimens provides dimens
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        AppNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
