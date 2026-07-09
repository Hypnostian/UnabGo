package co.edu.unab.sebastianlizcano.unabgo.domain.usecase

// Use Case (Clean Architecture) — encapsula la lógica "agregar un comentario"

import co.edu.unab.sebastianlizcano.unabgo.domain.repository.ITeachersRepository

class AddCommentUseCase(
    private val repository: ITeachersRepository // Dependency Inversion
) {
    /**
     * Agrega un comentario al docente indicado.
     * @return true si la operación fue exitosa.
     */
    suspend operator fun invoke(teacherId: String, text: String): Boolean =
        repository.addComment(teacherId, text)
}
