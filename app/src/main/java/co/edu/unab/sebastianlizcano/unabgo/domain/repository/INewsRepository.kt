package co.edu.unab.sebastianlizcano.unabgo.domain.repository

// Dependency Inversion Principle — interfaz del repositorio de noticias.

import co.edu.unab.sebastianlizcano.unabgo.data.remote.NewsItem

interface INewsRepository {

    /**
     * Obtiene noticias.
     *
     * @param categoryId  null = todas las noticias; o un ID de categoria UNAB.
     * @param perPage     cuántas por petición (default 15).
     * @return            lista de NewsItem listas para mostrar en la UI.
     * @throws Exception  si la red falla. El ViewModel lo captura.
     */
    suspend fun fetchNews(categoryId: Int? = null, perPage: Int = 15): List<NewsItem>

    /** Obtiene un único post por su ID (para la pantalla de detalle). */
    suspend fun fetchPostById(id: Long): NewsItem
}
