package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

data class Comment(
    val text: String = ""
)

class CommentsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var comments by mutableStateOf<List<Comment>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    fun loadComments(teacherId: String) {
        isLoading = true

        db.collection("teachers")
            .document(teacherId)
            .collection("comments")
            .orderBy("timestamp")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                comments = value?.documents?.map { doc ->
                    Comment(
                        text = doc.getString("text") ?: ""
                    )
                } ?: emptyList()

                isLoading = false
            }
    }
}
