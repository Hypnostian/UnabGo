package co.edu.unab.sebastianlizcano.unabgo.domain.repository

// Dependency Inversion Principle (DIP) — abstracción que desacopla dominio de datos
// Interface Segregation — define solo la operación del dominio Banu IA

interface IBanuRepository {

    /**
     * Envía la pregunta del usuario al modelo de IA y retorna la respuesta.
     * Lanza excepción si hay fallo de red o de API.
     */
    suspend fun ask(userQuestion: String): String
}
