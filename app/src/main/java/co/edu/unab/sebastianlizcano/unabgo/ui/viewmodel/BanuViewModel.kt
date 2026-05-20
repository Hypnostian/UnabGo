package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

// ViewModel (MVVM) — gestiona el estado de la pantalla Banu IA.
// Separation of Responsibilities — delega la llamada HTTP a BanuRepository.

import android.util.Log
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

    companion object { private const val TAG = "BanuViewModel" }

    // Observer Pattern (StateFlow)
    private val _answer  = MutableStateFlow<String?>(null)
    val answer           = _answer.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading          = _loading.asStateFlow()

    private val _error   = MutableStateFlow<String?>(null)
    val error            = _error.asStateFlow()

    /** Solicita una respuesta a Banu delegando al repositorio. */
    fun askBanu(userQuestion: String) {
        val q = userQuestion.trim()
        if (q.isBlank()) return

        viewModelScope.launch {
            _loading.value = true
            _error.value   = null
            _answer.value  = null
            try {
                val resp = repository.ask(q) // Repository Pattern
                _answer.value = resp
            } catch (e: Exception) {
                Log.e(TAG, "Error al consultar Banu", e)
                // El BanuRepository ya entrega mensajes amigables; los pasamos directo.
                _error.value = e.message
                    ?: "No se pudo conectar con Banu. Verifica tu internet e inténtalo nuevamente."
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
