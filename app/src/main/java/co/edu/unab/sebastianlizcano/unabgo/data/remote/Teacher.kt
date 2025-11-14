package co.edu.unab.sebastianlizcano.unabgo.data.remote

data class Teacher(
    val id: String = "",
    val fullName: String = "",
    val rating: Double = 0.0,
    val photoUrl: String? = null,
    val commentsCount: Int = 0
)
