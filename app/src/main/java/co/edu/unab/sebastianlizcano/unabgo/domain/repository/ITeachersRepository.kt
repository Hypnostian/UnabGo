package co.edu.unab.sebastianlizcano.unabgo.domain.repository

// Dependency Inversion Principle (DIP) — abstracción que desacopla dominio de datos
// Interface Segregation — define solo las operaciones del dominio Docentes

import co.edu.unab.sebastianlizcano.unabgo.data.remote.Comment
import co.edu.unab.sebastianlizcano.unabgo.data.remote.Teacher
import kotlinx.coroutines.flow.Flow

interface ITeachersRepository {

    /** Retorna un flujo reactivo con la lista de docentes en tiempo real. */
    fun getTeachersFlow(): Flow<List<Teacher>>

    /** Retorna un flujo reactivo con los comentarios de un docente. */
    fun getCommentsFlow(teacherId: String): Flow<List<Comment>>

    /** Agrega un nuevo comentario al docente indicado. Retorna true si tuvo éxito. */
    suspend fun addComment(teacherId: String, text: String): Boolean

    /** Actualiza la calificación del docente. Retorna true si tuvo éxito. */
    suspend fun updateRating(teacherId: String, rating: Double): Boolean
}
