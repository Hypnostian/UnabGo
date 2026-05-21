package co.edu.unab.sebastianlizcano.unabgo.data.repository

// Repository Pattern — encapsula el acceso a la WordPress REST API y mapea
// los DTOs JSON a NewsItem (modelo de dominio limpio para la UI).

import android.util.Log
import co.edu.unab.sebastianlizcano.unabgo.data.remote.NewsItem
import co.edu.unab.sebastianlizcano.unabgo.data.remote.UnabNewsApi
import co.edu.unab.sebastianlizcano.unabgo.data.remote.UnabNewsClient
import co.edu.unab.sebastianlizcano.unabgo.data.remote.WordPressPostDto
import co.edu.unab.sebastianlizcano.unabgo.domain.repository.INewsRepository
import java.text.SimpleDateFormat
import java.util.Locale

class NewsRepository(
    private val api: UnabNewsApi = UnabNewsClient.api   // Manual DI
) : INewsRepository {

    companion object { private const val TAG = "NewsRepository" }

    override suspend fun fetchNews(categoryId: Int?, perPage: Int): List<NewsItem> {
        Log.d(TAG, "Fetching news category=$categoryId perPage=$perPage")
        val posts = api.getPosts(categoryId = categoryId, perPage = perPage)
        return posts.map { it.toNewsItem() }
    }

    override suspend fun fetchPostById(id: Long): NewsItem {
        Log.d(TAG, "Fetching single post id=$id")
        return api.getPostById(id).toNewsItem()
    }

    // =============================================================
    // Helpers de mapeo DTO -> dominio
    // =============================================================

    private fun WordPressPostDto.toNewsItem(): NewsItem {
        // Imagen destacada: preferir tamaño "medium_large", luego "large", luego original
        val media = embedded?.featuredMedia?.firstOrNull()
        val sizes = media?.mediaDetails?.sizes
        val imageUrl = sizes?.get("medium_large")?.sourceUrl
            ?: sizes?.get("large")?.sourceUrl
            ?: sizes?.get("medium")?.sourceUrl
            ?: media?.sourceUrl

        return NewsItem(
            id           = id,
            title        = cleanHtml(title.rendered),
            excerpt      = cleanHtml(excerpt?.rendered ?: "").take(200),
            contentHtml  = content?.rendered ?: "",
            date         = formatDate(date),
            link         = link,
            imageUrl     = imageUrl,
            categoryIds  = categories
        )
    }

    /** Quita todas las etiquetas HTML y entidades comunes, devuelve texto plano. */
    private fun cleanHtml(html: String): String {
        return html
            .replace(Regex("<[^>]+>"), " ")
            .replace("&nbsp;", " ")
            .replace("&amp;",  "&")
            .replace("&lt;",   "<")
            .replace("&gt;",   ">")
            .replace("&quot;", "\"")
            .replace("&#39;",  "'")
            .replace("&hellip;", "…")
            .replace("&aacute;","á").replace("&eacute;","é").replace("&iacute;","í")
            .replace("&oacute;","ó").replace("&uacute;","ú").replace("&ntilde;","ñ")
            .replace("&Aacute;","Á").replace("&Eacute;","É").replace("&Iacute;","Í")
            .replace("&Oacute;","Ó").replace("&Uacute;","Ú").replace("&Ntilde;","Ñ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /** Convierte "2026-05-20T10:42:45" -> "20 may 2026". */
    private fun formatDate(iso: String): String = try {
        val input  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val output = SimpleDateFormat("dd MMM yyyy", Locale("es"))
        val date   = input.parse(iso)
        if (date != null) output.format(date) else iso.take(10)
    } catch (e: Exception) {
        iso.take(10)
    }
}
