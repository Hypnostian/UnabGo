package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

// ViewModel (MVVM) — gestiona el estado de la pantalla de Banu IA
// Separation of Responsibilities — delega la llamada HTTP a BanuRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.sebastianlizcano.unabgo.data.repository.BanuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Manual Dependency Injection — repositorio inyectado con valor por defecto
class BanuViewModel(
    private val repository: BanuRepository = BanuRepository() // Manual DI
) : ViewModel() {

    // Observer Pattern (StateFlow) — la UI observa estos flujos de estado
    private val _answer  = MutableStateFlow<String?>(null)
    val answer           = _answer.asStateFlow()    // Observer Pattern

    private val _loading = MutableStateFlow(false)
    val loading          = _loading.asStateFlow()   // Observer Pattern

    private val _error   = MutableStateFlow<String?>(null)
    val error            = _error.asStateFlow()     // Observer Pattern

    /** Solicita una respuesta a Banu delegando al repositorio. */
    fun askBanu(userQuestion: String) {
        if (userQuestion.isBlank()) return
        viewModelScope.launch {
            _loading.value = true
            _error.value   = null
            try {
                _answer.value = repository.ask(userQuestion) // Repository Pattern
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "No se pudo conectar con Banu. Verifica tu internet e inténtalo nuevamente."
            } finally {
                _loading.value = false
            }
        }
    }

    fun clear() {
        _answer.value = null
        _error.value  = null
    }
}
