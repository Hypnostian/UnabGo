package co.edu.unab.sebastianlizcano.unabgo.data.repository

// Repository Pattern — centraliza todo el acceso a Firestore del dominio Docentes
// Separation of Responsibilities — aísla la lógica de datos del ViewModel

import co.edu.unab.sebastianlizcano.unabgo.data.remote.Comment
import co.edu.unab.sebastianlizcano.unabgo.data.remote.Teacher
import co.edu.unab.sebastianlizcano.unabgo.domain.repository.ITeachersRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Manual Dependency Injection — db se inyecta con valor por defecto (Singleton de Firebase)
class TeachersRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance() // Singleton (Firebase)
) : ITeachersRepository { // Dependency Inversion Principle

    /**
     * Observer Pattern (Flow) — emite la lista de docentes en tiempo real.
     * Cancela el listener de Firestore cuando el Flow se cierra.
     */
    override fun getTeachersFlow(): Flow<List<Teacher>> = callbackFlow { // Observer Pattern
        val registration = db.collection("teachers")
            .addSnapshotListener { snapshots, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshots?.documents?.map { doc ->
                    Teacher(
                        id            = doc.id,
                        fullName      = doc.getString("fullName") ?: "",
                        rating        = doc.getDouble("rating") ?: 0.0,
                        photoUrl      = doc.getString("photoUrl"),
                        commentsCount = (doc.getLong("commentsCount") ?: 0L).toInt()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { registration.remove() } // Limpieza automática
    }

    /**
     * Observer Pattern (Flow) — emite los comentarios de un docente en tiempo real.
     */
    override fun getCommentsFlow(teacherId: String): Flow<List<Comment>> = callbackFlow { // Observer Pattern
        val registration = db.collection("teachers")
            .document(teacherId)
            .collection("comments")
            .orderBy("timestamp")
            .addSnapshotListener { value, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = value?.documents?.map { doc ->
                    Comment(text = doc.getString("text") ?: "")
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }

    /** Agrega un comentario y actualiza el contador de manera transaccional. */
    override suspend fun addComment(teacherId: String, text: String): Boolean =
        suspendCancellableCoroutine { cont ->
            val payload = hashMapOf(
                "text"      to text,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("teachers").document(teacherId).collection("comments")
                .add(payload)
                .addOnSuccessListener {
                    incrementCommentsCount(teacherId) // Transaction Pattern
                    cont.resume(true)
                }
                .addOnFailureListener { cont.resume(false) }
        }

    /** Transaction Pattern — incrementa el contador de forma atómica. */
    private fun incrementCommentsCount(teacherId: String) {
        val ref = db.collection("teachers").document(teacherId)
        db.runTransaction { tx -> // Transaction Pattern
            val current = tx.get(ref).getLong("commentsCount") ?: 0L
            tx.update(ref, "commentsCount", current + 1)
        }
    }

    /** Actualiza la calificación de un docente. */
    override suspend fun updateRating(teacherId: String, rating: Double): Boolean =
        suspendCancellableCoroutine { cont ->
            db.collection("teachers").document(teacherId)
                .update("rating", rating)
                .addOnSuccessListener { cont.resume(true) }
                .addOnFailureListener { cont.resume(false) }
        }
}
