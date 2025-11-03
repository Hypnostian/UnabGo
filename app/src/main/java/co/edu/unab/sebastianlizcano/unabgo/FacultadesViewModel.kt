package co.edu.unab.sebastianlizcano.unabgo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FacultadItem(
    val nombre: String,
    val url: String
)

sealed interface FacultadesUiState {
    object Loading : FacultadesUiState
    data class Ready(val items: List<FacultadItem>) : FacultadesUiState
    data class Error(val message: String) : FacultadesUiState
}

class FacultadesViewModel : ViewModel() {
    private val _state = MutableStateFlow<FacultadesUiState>(FacultadesUiState.Loading)
    val state = _state.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _state.value = FacultadesUiState.Loading
            try {
                // Buscamos páginas que contengan "Facultad" (API pública de WordPress)
                val pages = RetrofitClient.api.searchPages("Facultad")
                val items = pages.map { p ->
                    FacultadItem(
                        nombre = p.title.rendered.replace(Regex("<.*?>"), ""), // limpia HTML
                        url = p.link
                    )
                }.sortedBy { it.nombre }
                _state.value = FacultadesUiState.Ready(items)
            } catch (e: Exception) {
                //Imprime el error completo en Logcat y muestra el mensaje en pantalla
                e.printStackTrace()
                _state.value = FacultadesUiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }
}
