package co.edu.unab.sebastianlizcano.unabgo.FutureImplementation

import retrofit2.http.GET
import retrofit2.http.Query

// Respuesta mínima que necesitamos del WP REST API
data class WPPage(
    val id: Int,
    val link: String,
    val title: WPTitle
)

data class WPTitle(val rendered: String)

interface UnabApiService {
    // /wp-json/wp/v2/pages?search=Facultad
    @GET("wp-json/wp/v2/pages")
    suspend fun searchPages(@Query("search") query: String): List<WPPage>
}
