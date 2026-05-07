package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

// ViewModel (MVVM) — expone el estado de docentes a la UI
// Separation of Responsibilities — delega acceso a datos a TeachersRepository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.sebastianlizcano.unabgo.data.remote.Teacher
import co.edu.unab.sebastianlizcano.unabgo.data.repository.TeachersRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// Manual Dependency Injection — repositorio inyectado con valor por defecto
class TeachersViewModel(
    private val repository: TeachersRepository = TeachersRepository() // Manual DI
) : ViewModel() {

    // Observer Pattern (Compose State) — la UI se recompone al cambiar estos valores
    var teachers     by mutableStateOf<List<Teacher>>(emptyList()); private set
    var isLoading    by mutableStateOf(true);                        private set
    var errorMessage by mutableStateOf<String?>(null);               private set

    init { observeTeachers() }

    /** Observer Pattern — suscribe al Flow de Firestore y actualiza estado. */
    private fun observeTeachers() {
        viewModelScope.launch {
            repository.getTeachersFlow()          // Repository Pattern
                .catch { e ->
                    errorMessage = e.message
                    isLoading    = false
                }
                .collect { list ->
                    teachers  = list
                    isLoading = false
                }
        }
    }

    fun addComment(teacherId: String, text: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(repository.addComment(teacherId, text))
        }
    }

    fun updateRating(teacherId: String, rating: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(repository.updateRating(teacherId, rating))
        }
    }
}
