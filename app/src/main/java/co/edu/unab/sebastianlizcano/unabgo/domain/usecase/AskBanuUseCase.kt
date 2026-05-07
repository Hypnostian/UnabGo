package co.edu.unab.sebastianlizcano.unabgo.domain.usecase

// Use Case (Clean Architecture) — encapsula la lógica "consultar a Banu IA"

import co.edu.unab.sebastianlizcano.unabgo.domain.repository.IBanuRepository

class AskBanuUseCase(
    private val repository: IBanuRepository // Dependency Inversion
) {
    /**
     * Envía la pregunta del usuario al modelo de IA y retorna la respuesta.
     * Lanza excepción si hay fallo de red.
     */
    suspend operator fun invoke(question: String): String = repository.ask(question)
}
