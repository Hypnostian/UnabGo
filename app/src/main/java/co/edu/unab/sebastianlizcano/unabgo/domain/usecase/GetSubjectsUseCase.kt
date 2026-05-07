package co.edu.unab.sebastianlizcano.unabgo.domain.usecase

// Use Case (Clean Architecture) — encapsula la lógica "obtener materias del usuario"

import co.edu.unab.sebastianlizcano.unabgo.data.local.SubjectEntity
import co.edu.unab.sebastianlizcano.unabgo.domain.repository.IAcademicRepository
import kotlinx.coroutines.flow.Flow

class GetSubjectsUseCase(
    private val repository: IAcademicRepository // Dependency Inversion
) {
    /** Retorna el flujo reactivo de materias para el usuario dado. */
    operator fun invoke(userId: String): Flow<List<SubjectEntity>> =
        repository.getSubjects(userId)
}
