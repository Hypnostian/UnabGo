package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.rememberNavController
import co.edu.unab.sebastianlizcano.unabgo.data.local.LanguageDataStore
import co.edu.unab.sebastianlizcano.unabgo.navigation.AppNavHost
import co.edu.unab.sebastianlizcano.unabgo.ui.theme.AppDimens
import co.edu.unab.sebastianlizcano.unabgo.ui.theme.LocalAppDimens
import co.edu.unab.sebastianlizcano.unabgo.utils.LocaleManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

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
        // =====================================================
        // FORZAR PORTRAIT EN RUNTIME — ANTES de super.onCreate()
        // Algunos OEM (Xiaomi/HyperOS) y Android 16 pueden ignorar el
        // screenOrientation del Manifest. Hacerlo aqui programaticamente
        // garantiza que la app SIEMPRE arranque en portrait.
        // =====================================================
        @Suppress("SourceLockedOrientationActivity")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)

        // FCM token wrapped en try/catch (Firebase puede estar inactivo)
        try {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("UNABGO", "Token FCM no disponible: ${task.exception?.message}")
                        return@addOnCompleteListener
                    }
                    Log.d("UNABGO", "TOKEN FCM: ${task.result}")
                }
        } catch (e: Exception) {
            Log.w("UNABGO", "Firebase Messaging no disponible: ${e.message}")
        }

        // PERMISO DE NOTIFICACIONES (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 1001)
            }
        }

        // INTERFAZ COMPOSE
        setContent {

            // =====================================================
            // NO USAR calculateWindowSizeClass: ese cálculo le declara
            // al sistema que la app se adapta a múltiples tamaños, lo
            // cual entra en conflicto con screenOrientation=portrait y
            // DISPARA el overlay "Gira tu teléfono para ver el contenido"
            // en Android 14/15/16 (Pixel emulator, Xiaomi, Samsung, etc).
            //
            // En su lugar, decidimos las dimensiones segun el ancho REAL
            // en dp del LocalConfiguration. Asi la app sigue siendo
            // responsive sin declararlo al sistema.
            // =====================================================
            val configuration = LocalConfiguration.current
            val widthDp       = configuration.screenWidthDp

            val (fontScale, dimens) = when {
                widthDp < 600  -> 0.92f to AppDimens(
                    titleXL = 38f, titleL = 20f, body = 13f,
                    buttonHeight = 50, logoSize = 76, heroImageSize = 190,
                    gapS = 6, gapM = 12, gapL = 24
                )
                widthDp < 840  -> 1.0f  to AppDimens(
                    titleXL = 45f, titleL = 22f, body = 14f,
                    buttonHeight = 55, logoSize = 90, heroImageSize = 210,
                    gapS = 8, gapM = 16, gapL = 32
                )
                else           -> 1.12f to AppDimens(
                    titleXL = 52f, titleL = 24f, body = 16f,
                    buttonHeight = 60, logoSize = 110, heroImageSize = 240,
                    gapS = 10, gapM = 20, gapL = 40
                )
            }

            val navController = rememberNavController()
            val context       = this
            val dataStore     = remember { LanguageDataStore(context) }

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

    // Refuerzo: si Android cambia la configuracion (e.g. multi-window forzado),
    // volvemos a pedir portrait inmediatamente.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        @Suppress("SourceLockedOrientationActivity")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
