package co.edu.unab.sebastianlizcano.unabgo.domain.repository

// Dependency Inversion Principle (DIP) — abstracción que desacopla dominio de datos académicos
// Interface Segregation — define solo las operaciones del dominio Académico

import co.edu.unab.sebastianlizcano.unabgo.data.local.GradeCategoryEntity
import co.edu.unab.sebastianlizcano.unabgo.data.local.GradeItemEntity
import co.edu.unab.sebastianlizcano.unabgo.data.local.ScheduleBlockEntity
import co.edu.unab.sebastianlizcano.unabgo.data.local.SubjectEntity
import co.edu.unab.sebastianlizcano.unabgo.data.local.SubjectWithGrades
import co.edu.unab.sebastianlizcano.unabgo.data.local.SubjectWithSchedule
import kotlinx.coroutines.flow.Flow

interface IAcademicRepository {

    // ---- Materias ----
    fun getSubjects(userId: String): Flow<List<SubjectEntity>>
    fun getSubjectWithSchedule(subjectId: Long): Flow<SubjectWithSchedule>
    fun getSubjectWithGrades(subjectId: Long): Flow<SubjectWithGrades>
    suspend fun insertSubjectReturningId(subject: SubjectEntity): Long
    suspend fun updateSubject(subject: SubjectEntity)
    suspend fun deleteSubject(subject: SubjectEntity)

    // ---- Horarios ----
    fun getBlocksForSubject(subjectId: Long): Flow<List<ScheduleBlockEntity>>
    suspend fun insertBlock(block: ScheduleBlockEntity)
    suspend fun updateBlock(block: ScheduleBlockEntity)
    suspend fun deleteBlock(block: ScheduleBlockEntity)

    // ---- Categorías ----
    suspend fun insertCategory(cat: GradeCategoryEntity): Long
    suspend fun updateCategory(cat: GradeCategoryEntity)
    suspend fun deleteCategory(cat: GradeCategoryEntity)

    // ---- Ítems de nota ----
    suspend fun insertItem(item: GradeItemEntity): Long
    suspend fun updateItem(item: GradeItemEntity)
    suspend fun deleteItem(item: GradeItemEntity)

    // ---- Cálculos ----
    fun computeSubjectAverage(subject: SubjectWithGrades): Float?
    fun computeGlobalAverage(subjects: List<SubjectEntity>, subjectAverages: Map<Long, Float?>): Float?
}
