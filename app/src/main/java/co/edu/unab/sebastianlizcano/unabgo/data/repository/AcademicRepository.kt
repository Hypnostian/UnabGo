package co.edu.unab.sebastianlizcano.unabgo.data.repository

import co.edu.unab.sebastianlizcano.unabgo.data.local.*
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

class AcademicRepository(
    private val subjectDao: SubjectDao,
    private val scheduleDao: ScheduleDao,
    private val gradesDao: GradesDao
) {

    // ---------------------------------------------------------
    // MATERIAS
    // ---------------------------------------------------------

    fun getSubjects(userId: String): Flow<List<SubjectEntity>> {
        return subjectDao.getSubjectsForUser(userId)
    }

    fun getSubjectWithSchedule(subjectId: Long): Flow<SubjectWithSchedule> {
        return subjectDao.getSubjectWithSchedule(subjectId)
    }

    fun getSubjectWithGrades(subjectId: Long): Flow<SubjectWithGrades> {
        return subjectDao.getSubjectWithGrades(subjectId)
    }

    suspend fun insertSubject(subject: SubjectEntity): Long {
        return subjectDao.insertSubject(subject)
    }

    suspend fun insertSubjectReturningId(subject: SubjectEntity): Long {
        return subjectDao.insertReturningId(subject)
    }

    suspend fun updateSubject(subject: SubjectEntity) {
        subjectDao.updateSubject(subject)
    }

    suspend fun deleteSubject(subject: SubjectEntity) {
        // Room se encargará de borrar categorías y horarios automáticamente
        subjectDao.deleteSubject(subject)
    }


    // ---------------------------------------------------------
    //HORARIOS
    // ---------------------------------------------------------

    fun getBlocksForSubject(subjectId: Long): Flow<List<ScheduleBlockEntity>> {
        return scheduleDao.getBlocksForSubject(subjectId)
    }

    suspend fun insertBlock(block: ScheduleBlockEntity) {
        scheduleDao.insertBlock(block)
    }

    suspend fun updateBlock(block: ScheduleBlockEntity) {
        scheduleDao.updateBlock(block)
    }

    suspend fun deleteBlock(block: ScheduleBlockEntity) {
        scheduleDao.deleteBlock(block)
    }


    // ---------------------------------------------------------
    //CATEGORÍAS
    // ---------------------------------------------------------

    fun getCategories(subjectId: Long): Flow<List<GradeCategoryEntity>> {
        return gradesDao.getCategoriesForSubject(subjectId)
    }

    suspend fun insertCategory(cat: GradeCategoryEntity): Long {
        return gradesDao.insertCategory(cat)
    }

    suspend fun updateCategory(cat: GradeCategoryEntity) {
        gradesDao.updateCategory(cat)
    }

    suspend fun deleteCategory(cat: GradeCategoryEntity) {
        gradesDao.deleteCategory(cat)
    }

    // ---------------------------------------------------------
    //ÍTEMS DE NOTA
    // ---------------------------------------------------------

    fun getItems(categoryId: Long): Flow<List<GradeItemEntity>> {
        return gradesDao.getItemsForCategory(categoryId)
    }

    suspend fun insertItem(item: GradeItemEntity): Long {
        return gradesDao.insertItem(item)
    }

    suspend fun updateItem(item: GradeItemEntity) {
        gradesDao.updateItem(item)
    }

    suspend fun deleteItem(item: GradeItemEntity) {
        gradesDao.deleteItem(item)
    }

    // ---------------------------------------------------------
    //CÁLCULOS DE PROMEDIOS
    // ---------------------------------------------------------

    /**
     * Calcula el promedio final de *una* materia.
     *
     * Fórmula:
     *  promedioMateria = sum( (notaItem * pesoEnCategoria/100) * (pesoCategoria/100) )
     *
     * Devuelve null si no tiene notas todavía.
     */
    fun computeSubjectAverage(subject: SubjectWithGrades): Float? {
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
    fun computeGlobalAverage(
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
