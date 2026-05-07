package co.edu.unab.sebastianlizcano.unabgo.domain.usecase

// Use Case (Clean Architecture) — encapsula la lógica "obtener comentarios de un docente"

import co.edu.unab.sebastianlizcano.unabgo.data.remote.Comment
import co.edu.unab.sebastianlizcano.unabgo.domain.repository.ITeachersRepository
import kotlinx.coroutines.flow.Flow

class GetCommentsUseCase(
    private val repository: ITeachersRepository // Dependency Inversion
) {
    /** Retorna el flujo reactivo de comentarios para el docente dado. */
    operator fun invoke(teacherId: String): Flow<List<Comment>> =
        repository.getCommentsFlow(teacherId)
}
