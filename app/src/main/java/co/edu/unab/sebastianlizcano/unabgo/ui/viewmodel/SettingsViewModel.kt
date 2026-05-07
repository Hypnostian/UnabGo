package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

// ViewModel (MVVM) — gestiona configuración de usuario (idioma)
// Separation of Responsibilities — aísla la lógica de DataStore del Composable

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
     * Guarda el nuevo idioma en DataStore y aplica el cambio al contexto.
     */
    fun setLanguage(context: Context, langCode: String) {
        viewModelScope.launch {
            val dataStore = LanguageDataStore(context)
            dataStore.saveLanguage(langCode)
            _selectedLanguage.value = langCode
            LocaleManager.setLocale(context, langCode) // Singleton (LocaleManager)
        }
    }
}
