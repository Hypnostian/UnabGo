package co.edu.unab.sebastianlizcano.unabgo

// Application class — punto de entrada del ciclo de vida de la app
// Singleton (Application) — instancia única gestionada por Android
// Manual Dependency Injection — centraliza la creación de dependencias globales

import android.app.Application
import co.edu.unab.sebastianlizcano.unabgo.data.local.UnabGoDatabase
import co.edu.unab.sebastianlizcano.unabgo.data.repository.AcademicRepository
import co.edu.unab.sebastianlizcano.unabgo.data.repository.BanuRepository
import co.edu.unab.sebastianlizcano.unabgo.data.repository.TeachersRepository
import co.edu.unab.sebastianlizcano.unabgo.domain.usecase.AddCommentUseCase
import co.edu.unab.sebastianlizcano.unabgo.domain.usecase.AskBanuUseCase
import co.edu.unab.sebastianlizcano.unabgo.domain.usecase.GetCommentsUseCase
import co.edu.unab.sebastianlizcano.unabgo.domain.usecase.GetSubjectsUseCase
import co.edu.unab.sebastianlizcano.unabgo.domain.usecase.GetTeachersUseCase

class UnabGoApplication : Application() { // Singleton (Application)

    // ----------------------------------------------------------------
    // Repositorios — instancias únicas compartidas en toda la app
    // ----------------------------------------------------------------

    /** Instancia compartida de TeachersRepository (evita múltiples conexiones Firestore). */
    val teachersRepository: TeachersRepository by lazy { // Lazy Initialization
        TeachersRepository() // Manual DI
    }

    /** Instancia compartida de BanuRepository. */
    val banuRepository: BanuRepository by lazy {
        BanuRepository()
    }

    /** Instancia compartida de AcademicRepository, construida a partir de la DB Room. */
    val academicRepository: AcademicRepository by lazy {
        val db = UnabGoDatabase.getInstance(this) // Singleton (Double-Check Locking)
        AcademicRepository(
            subjectDao  = db.subjectDao(),
            scheduleDao = db.scheduleDao(),
            gradesDao   = db.gradesDao()
        )
    }

    // ----------------------------------------------------------------
    // Use Cases — lógica de negocio de la capa de dominio
    // ----------------------------------------------------------------

    val getTeachersUseCase: GetTeachersUseCase by lazy {
        GetTeachersUseCase(teachersRepository)
    }

    val getCommentsUseCase: GetCommentsUseCase by lazy {
        GetCommentsUseCase(teachersRepository)
    }

    val addCommentUseCase: AddCommentUseCase by lazy {
        AddCommentUseCase(teachersRepository)
    }

    val askBanuUseCase: AskBanuUseCase by lazy {
        AskBanuUseCase(banuRepository)
    }

    val getSubjectsUseCase: GetSubjectsUseCase by lazy {
        GetSubjectsUseCase(academicRepository)
    }
}
