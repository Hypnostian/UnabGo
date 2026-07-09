package co.edu.unab.sebastianlizcano.unabgo.domain.usecase

// Use Case (Clean Architecture) — encapsula la lógica de negocio "obtener docentes"
// Single Responsibility Principle — una clase, una razón para cambiar

import co.edu.unab.sebastianlizcano.unabgo.data.remote.Teacher
import co.edu.unab.sebastianlizcano.unabgo.domain.repository.ITeachersRepository
import kotlinx.coroutines.flow.Flow

class GetTeachersUseCase(
    private val repository: ITeachersRepository // Dependency Inversion — depende de la abstracción
) {
    /** Retorna el flujo reactivo de docentes. */
    operator fun invoke(): Flow<List<Teacher>> = repository.getTeachersFlow()
}
