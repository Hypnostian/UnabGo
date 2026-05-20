package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

// ViewModel (MVVM) — gestiona la configuración de usuario (idioma).
// Separation of Responsibilities — aísla la lógica de DataStore del Composable.

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.sebastianlizcano.unabgo.data.local.LanguageDataStore
import co.edu.unab.sebastianlizcano.unabgo.utils.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() { // ViewModel (MVVM)

    // Observer Pattern (StateFlow) — la UI observa el idioma seleccionado
    private val _selectedLanguage = MutableStateFlow("es")
    val selectedLanguage = _selectedLanguage.asStateFlow()

    /**
     * Carga el idioma guardado en DataStore al iniciar la pantalla.
     */
    fun loadLanguage(context: Context) {
        viewModelScope.launch {
            val dataStore = LanguageDataStore(context) // DataStore Pattern
            dataStore.getLanguage().collect { lang ->
                _selectedLanguage.value = lang ?: "es"
            }
        }
    }

    /**
     * Guarda el nuevo idioma en DataStore + SharedPreferences (LocaleManager)
     * y notifica a través del StateFlow.
     *
     * NOTA: el cambio visual de la UI requiere recrear la Activity después
     * de que la persistencia haya terminado. Eso se delega al callback `onSaved`
     * que el Composable invoca para llamar a (context as Activity).recreate().
     */
    fun setLanguage(context: Context, langCode: String, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            // 1) Persistir en DataStore (lo lee MainActivity.attachBaseContext)
            val dataStore = LanguageDataStore(context)
            dataStore.saveLanguage(langCode)

            // 2) Persistir en SharedPreferences (lo lee LocaleManager.loadLocale)
            LocaleManager.setLocale(context, langCode)

            // 3) Notificar al StateFlow
            _selectedLanguage.value = langCode

            // 4) Pedir a la UI que recree la Activity para aplicar el locale
            onSaved()
        }
    }
}
