package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

// ViewModel (MVVM) — expone comentarios de un docente a la UI
// Separation of Responsibilities — delega acceso a Firestore a TeachersRepository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.sebastianlizcano.unabgo.data.remote.Comment
import co.edu.unab.sebastianlizcano.unabgo.data.repository.TeachersRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// Manual Dependency Injection — repositorio inyectado con valor por defecto
class CommentsViewModel(
    private val repository: TeachersRepository = TeachersRepository() // Manual DI
) : ViewModel() {

    // Observer Pattern (Compose State)
    var comments  by mutableStateOf<List<Comment>>(emptyList()); private set
    var isLoading by mutableStateOf(true);                       private set

    /** Observer Pattern — se suscribe al Flow de comentarios en tiempo real. */
    fun loadComments(teacherId: String) {
        isLoading = true
        viewModelScope.launch {
            repository.getCommentsFlow(teacherId) // Repository Pattern
                .catch { isLoading = false }
                .collect { list ->
                    comments  = list
                    isLoading = false
                }
        }
    }
}
