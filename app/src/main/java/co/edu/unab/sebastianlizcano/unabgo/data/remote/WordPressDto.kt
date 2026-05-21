package co.edu.unab.sebastianlizcano.unabgo.data.remote

import com.google.gson.annotations.SerializedName

/**
 * DTOs (Data Transfer Objects) que mapean la respuesta JSON de la API REST de
 * WordPress de https://unab.edu.co/wp-json/wp/v2/posts?_embed=true
 *
 * Solo declaramos los campos que realmente usamos en la app.
 */

data class WordPressPostDto(
    val id: Long,
    val date: String,          // ISO: "2026-05-20T10:42:45"
    val link: String,          // URL original del post
    val title: RenderedText,
    val excerpt: RenderedText? = null,
    val content: RenderedText? = null,
    val categories: List<Int> = emptyList(),

    @SerializedName("_embedded")
    val embedded: Embedded? = null
)

data class RenderedText(
    val rendered: String
)

/**
 * El _embedded contiene la imagen destacada (wp:featuredmedia)
 * y los términos/categorias (wp:term).
 */
data class Embedded(
    @SerializedName("wp:featuredmedia")
    val featuredMedia: List<FeaturedMedia>? = null
)

data class FeaturedMedia(
    @SerializedName("source_url")
    val sourceUrl: String? = null,

    @SerializedName("media_details")
    val mediaDetails: MediaDetails? = null
)

data class MediaDetails(
    val sizes: Map<String, MediaSize>? = null
)

data class MediaSize(
    val width: Int? = null,
    val height: Int? = null,

    @SerializedName("source_url")
    val sourceUrl: String? = null
)

// =============================================================
// MODELO de DOMINIO que usa la UI (limpio, sin tipos de Gson)
// =============================================================

data class NewsItem(
    val id: Long,
    val title: String,
    val excerpt: String,      // texto plano sin HTML
    val contentHtml: String,  // HTML del articulo completo
    val date: String,         // formato bonito tipo "20 may 2026"
    val link: String,         // URL original
    val imageUrl: String?,    // imagen destacada (mejor calidad disponible)
    val categoryIds: List<Int>
)
