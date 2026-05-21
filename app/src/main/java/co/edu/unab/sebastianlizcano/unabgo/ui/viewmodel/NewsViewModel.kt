package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

// ViewModel (MVVM) — gestiona la lista de noticias UNAB y su categoria activa.
// Estados claros (Loading / Success / Error) para que la UI los maneje.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.sebastianlizcano.unabgo.data.remote.NewsItem
import co.edu.unab.sebastianlizcano.unabgo.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** IDs reales de las categorías oficiales UNAB (verificados con la API). */
object UnabCategoryIds {
    const val ALL                      : Int? = null  // sin filtro = todas
    const val ACTUALIDAD_INSTITUCIONAL  = 2
    const val INVESTIGACION             = 35
    const val ARTE_CULTURA              = 202
    const val HISTORIAS_CON_IMPACTO     = 204
}

/** Estado de la pantalla. Sealed class para forzar el manejo de todos los casos. */
sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val items: List<NewsItem>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

class NewsViewModel(
    private val repository: NewsRepository = NewsRepository()  // Manual DI
) : ViewModel() {

    private val _state = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val state = _state.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Int?>(UnabCategoryIds.ALL)
    val selectedCategory = _selectedCategory.asStateFlow()

    /** Cambia la categoría activa y vuelve a cargar. */
    fun selectCategory(categoryId: Int?) {
        if (_selectedCategory.value == categoryId &&
            _state.value is NewsUiState.Success) return
        _selectedCategory.value = categoryId
        load()
    }

    /** Carga (o recarga) las noticias para la categoría actual. */
    fun load() {
        viewModelScope.launch {
            _state.value = NewsUiState.Loading
            try {
                val items = repository.fetchNews(
                    categoryId = _selectedCategory.value,
                    perPage    = 20
                )
                _state.value = NewsUiState.Success(items)
            } catch (e: Exception) {
                _state.value = NewsUiState.Error(
                    e.localizedMessage
                        ?: "No se pudieron cargar las noticias. Verifica tu conexión a internet."
                )
            }
        }
    }

    init {
        load()
    }
}

// =============================================================
// ViewModel separado para la pantalla de DETALLE
// =============================================================

sealed class NewsDetailState {
    object Loading : NewsDetailState()
    data class Success(val item: co.edu.unab.sebastianlizcano.unabgo.data.remote.NewsItem) : NewsDetailState()
    data class Error(val message: String) : NewsDetailState()
}

class NewsDetailViewModel(
    private val repository: NewsRepository = NewsRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<NewsDetailState>(NewsDetailState.Loading)
    val state = _state.asStateFlow()

    fun load(postId: Long) {
        viewModelScope.launch {
            _state.value = NewsDetailState.Loading
            try {
                val item = repository.fetchPostById(postId)
                _state.value = NewsDetailState.Success(item)
            } catch (e: Exception) {
                _state.value = NewsDetailState.Error(
                    e.localizedMessage ?: "No se pudo cargar la noticia."
                )
            }
        }
    }
}
