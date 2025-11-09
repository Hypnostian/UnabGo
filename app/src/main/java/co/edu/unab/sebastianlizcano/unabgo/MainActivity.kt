package co.edu.unab.sebastianlizcano.unabgo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Cargar el idioma guardado antes de que se cree la app
        val dataStore = LanguageDataStore(newBase)

        // Obtener el idioma guardado sin bloquear indefinidamente
        val savedLang = runBlocking {
            dataStore.getLanguage().first() ?: "es"
        }

        // Aplicar el idioma guardado
        val context = LocaleManager.loadLocale(newBase, savedLang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = this
            val dataStore = remember { LanguageDataStore(context) }
            val scope = rememberCoroutineScope()

            // Estado global del idioma actual
            var currentLang by remember { mutableStateOf(LocaleManager.getCurrentLanguage(context)) }

            // Escuchar cambios en el idioma y actualizar la UI al instante
            LaunchedEffect(Unit) {
                dataStore.getLanguage().collect { lang ->
                    if (lang != null && lang != currentLang) {
                        currentLang = lang
                        LocaleManager.setLocale(context, lang)
                    }
                }
            }

            // Recompone toda la UI cada vez que cambia el idioma
            key(currentLang) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}
