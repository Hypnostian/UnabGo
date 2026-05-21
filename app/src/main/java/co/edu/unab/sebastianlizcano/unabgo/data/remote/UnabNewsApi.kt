package co.edu.unab.sebastianlizcano.unabgo.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Retrofit service para la API REST de WordPress de la UNAB.
 * Endpoint base: https://unab.edu.co/wp-json/wp/v2/
 */
interface UnabNewsApi {

    /**
     * Lista posts. Si categoryId es null, devuelve los más recientes de TODAS
     * las categorías. _embed=true incluye la imagen destacada.
     */
    @GET("posts")
    suspend fun getPosts(
        @Query("categories") categoryId: Int? = null,
        @Query("per_page")   perPage: Int = 15,
        @Query("page")       page: Int = 1,
        @Query("_embed")     embed: Boolean = true
    ): List<WordPressPostDto>

    /** Obtiene UN solo post por ID (usado por la pantalla de detalle). */
    @GET("posts/{id}")
    suspend fun getPostById(
        @retrofit2.http.Path("id") id: Long,
        @Query("_embed") embed: Boolean = true
    ): WordPressPostDto
}

/**
 * Singleton (Lazy Initialization + Builder Pattern) para acceder a la API.
 */
object UnabNewsClient {

    private const val BASE_URL = "https://unab.edu.co/wp-json/wp/v2/"

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val api: UnabNewsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnabNewsApi::class.java)
    }
}
