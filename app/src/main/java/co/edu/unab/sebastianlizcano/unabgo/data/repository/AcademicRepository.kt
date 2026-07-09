package co.edu.unab.sebastianlizcano.unabgo.data.repository

import co.edu.unab.sebastianlizcano.unabgo.data.local.*
import co.edu.unab.sebastianlizcano.unabgo.domain.repository.IAcademicRepository
import kotlinx.coroutines.flow.*
import kotlin.math.round

/**
 * Repositorio central para:
 * - Materias
 * - Horarios
 * - Categorías de notas
 * - Ítems de notas
 *
 * Une los DAOs para exponer funciones completas hacia el ViewModel.
 */

class AcademicRepository( // Repository Pattern — fuente única de verdad para datos académicos locales
    private val subjectDao: SubjectDao,
    private val scheduleDao: ScheduleDao,
    private val gradesDao: GradesDao
) : IAcademicRepository { // Dependency Inversion Principle

    // ---------------------------------------------------------
    // MATERIAS
    // ---------------------------------------------------------

    override fun getSubjects(userId: String): Flow<List<SubjectEntity>> {
        return subjectDao.getSubjectsForUser(userId)
    }

    override fun getSubjectWithSchedule(subjectId: Long): Flow<SubjectWithSchedule> {
        return subjectDao.getSubjectWithSchedule(subjectId)
    }

    override fun getSubjectWithGrades(subjectId: Long): Flow<SubjectWithGrades> {
        return subjectDao.getSubjectWithGrades(subjectId)
    }

    // Método extra no declarado en la interfaz (mantiene compatibilidad hacia atrás)
    suspend fun insertSubject(subject: SubjectEntity): Long {
        return subjectDao.insertSubject(subject)
    }

    override suspend fun insertSubjectReturningId(subject: SubjectEntity): Long {
        return subjectDao.insertReturningId(subject)
    }

    override suspend fun updateSubject(subject: SubjectEntity) {
        subjectDao.updateSubject(subject)
    }

    override suspend fun deleteSubject(subject: SubjectEntity) {
        // Room se encargará de borrar categorías y horarios automáticamente
        subjectDao.deleteSubject(subject)
    }


    // ---------------------------------------------------------
    // HORARIOS
    // ---------------------------------------------------------

    override fun getBlocksForSubject(subjectId: Long): Flow<List<ScheduleBlockEntity>> {
        return scheduleDao.getBlocksForSubject(subjectId)
    }

    override suspend fun insertBlock(block: ScheduleBlockEntity) {
        scheduleDao.insertBlock(block)
    }

    override suspend fun updateBlock(block: ScheduleBlockEntity) {
        scheduleDao.updateBlock(block)
    }

    override suspend fun deleteBlock(block: ScheduleBlockEntity) {
        scheduleDao.deleteBlock(block)
    }


    // ---------------------------------------------------------
    // CATEGORÍAS
    // ---------------------------------------------------------

    fun getCategories(subjectId: Long): Flow<List<GradeCategoryEntity>> {
        return gradesDao.getCategoriesForSubject(subjectId)
    }

    override suspend fun insertCategory(cat: GradeCategoryEntity): Long {
        return gradesDao.insertCategory(cat)
    }

    override suspend fun updateCategory(cat: GradeCategoryEntity) {
        gradesDao.updateCategory(cat)
    }

    override suspend fun deleteCategory(cat: GradeCategoryEntity) {
        gradesDao.deleteCategory(cat)
    }

    // ---------------------------------------------------------
    // ÍTEMS DE NOTA
    // ---------------------------------------------------------

    fun getItems(categoryId: Long): Flow<List<GradeItemEntity>> {
        return gradesDao.getItemsForCategory(categoryId)
    }

    override suspend fun insertItem(item: GradeItemEntity): Long {
        return gradesDao.insertItem(item)
    }

    override suspend fun updateItem(item: GradeItemEntity) {
        gradesDao.updateItem(item)
    }

    override suspend fun deleteItem(item: GradeItemEntity) {
        gradesDao.deleteItem(item)
    }

    // ---------------------------------------------------------
    // CÁLCULOS DE PROMEDIOS
    // ---------------------------------------------------------

    /**
     * Calcula el promedio final de *una* materia.
     *
     * Fórmula:
     *  promedioMateria = sum( (notaItem * pesoEnCategoria/100) * (pesoCategoria/100) )
     *
     * Devuelve null si no tiene notas todavía.
     */
    override fun computeSubjectAverage(subject: SubjectWithGrades): Float? {
        if (subject.categories.isEmpty()) return null

        var total = 0f

        for (cat in subject.categories) {

            if (cat.items.isEmpty()) continue

            val catWeight = cat.category.weightInFinal / 100f

            var categoryAccum = 0f

            for (item in cat.items) {
                val itemWeight = item.weightInCategory / 100f
                categoryAccum += item.grade * itemWeight
            }

            total += categoryAccum * catWeight
        }

        if (total == 0f) return null

        return (round(total * 100) / 100f)
    }


    /**
     * Calcula el promedio general según créditos:
     *
     * promedioGeneral = sum(promedioMateria * creditos) / sum(creditos)
     */
    override fun computeGlobalAverage(
        subjects: List<SubjectEntity>,
        subjectAverages: Map<Long, Float?>
    ): Float? {
        var totalWeighted = 0f
        var totalCredits = 0

        for (sub in subjects) {
            val avg = subjectAverages[sub.id] ?: continue
            totalWeighted += avg * sub.credits
            totalCredits += sub.credits
        }

        if (totalCredits == 0) return null

        val result = totalWeighted / totalCredits
        return (round(result * 100) / 100f)
    }
}
