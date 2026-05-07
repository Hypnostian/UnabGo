package co.edu.unab.sebastianlizcano.unabgo.future
/*
import co.edu.unab.sebastianlizcano.unabgo.NewsArticle

class NewsRepository(private val api: NewsApi) {

    suspend fun getAllNews(): List<NewsArticle> {
        return api.getNews()
    }

    suspend fun getNewsByCategory(category: String): List<NewsArticle> {
        val all = api.getNews()
        return all.filter { it.category == category }
    }

    suspend fun getNewsById(id: String): NewsArticle? {
        val all = api.getNews()
        return all.find { it.id == id }
    }
}
*/