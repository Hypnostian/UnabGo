package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import co.edu.unab.sebastianlizcano.unabgo.data.remote.Teacher

class TeachersViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var teachers by mutableStateOf<List<Teacher>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var registration: ListenerRegistration? = null

    init {
        subscribeTeachers()
    }

    /**
     * Escucha en tiempo real la lista de profesores
     */
    private fun subscribeTeachers() {
        registration?.remove()

        registration = db.collection("teachers")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    errorMessage = e.message
                    isLoading = false
                    return@addSnapshotListener
                }

                val list = snapshots?.documents?.map { doc ->
                    Teacher(
                        id = doc.id,
                        fullName = doc.getString("fullName") ?: "",
                        rating = doc.getDouble("rating") ?: 0.0,
                        photoUrl = doc.getString("photoUrl"),
                        commentsCount = (doc.getLong("commentsCount") ?: 0L).toInt()
                    )
                } ?: emptyList()

                teachers = list
                isLoading = false
            }
    }

    /**
     * AGREGAR COMENTARIO A UN DOCENTE
     * teachers/{teacherId}/comments/{autoId}
     */
    fun addComment(teacherId: String, text: String, onResult: (Boolean) -> Unit) {
        if (text.isBlank()) {
            onResult(false)
            return
        }

        val comment = hashMapOf(
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )

        // Guardar comentario
        db.collection("teachers")
            .document(teacherId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener {
                // Actualizar contador de comentarios en teachers/{id}
                incrementCommentsCount(teacherId)
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    /**
     * SUMAR UN COMENTARIO AL CONTADOR
     */
    private fun incrementCommentsCount(teacherId: String) {
        val teacherRef = db.collection("teachers").document(teacherId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(teacherRef)
            val current = snapshot.getLong("commentsCount") ?: 0L
            transaction.update(teacherRef, "commentsCount", current + 1)
        }
    }

    /**
     * ACTUALIZAR CALIFICACIÓN (RATING)
     */
    fun updateRating(teacherId: String, rating: Double, onResult: (Boolean) -> Unit) {
        db.collection("teachers")
            .document(teacherId)
            .update("rating", rating)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    override fun onCleared() {
        registration?.remove()
        super.onCleared()
    }
}
