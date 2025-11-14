package co.edu.unab.sebastianlizcano.unabgo.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * ---------------------------------------------------------
 *                   DAO: Subjects
 * ---------------------------------------------------------
 */

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects WHERE userId = :userId ORDER BY name ASC")
    fun getSubjectsForUser(userId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    fun getSubjectById(id: Long): Flow<SubjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturningId(entity: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    /**
     * Para traer horarios + materia juntos
     */
    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :subjectId LIMIT 1")
    fun getSubjectWithSchedule(subjectId: Long): Flow<SubjectWithSchedule>

    /**
     * Para traer categorías + ítems juntos
     */
    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :subjectId LIMIT 1")
    fun getSubjectWithGrades(subjectId: Long): Flow<SubjectWithGrades>
}

/**
 * ---------------------------------------------------------
 *                   DAO: Schedule Blocks
 * ---------------------------------------------------------
 */

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedule_blocks WHERE subjectId = :subjectId")
    fun getBlocksForSubject(subjectId: Long): Flow<List<ScheduleBlockEntity>>

    @Query("SELECT * FROM schedule_blocks WHERE dayOfWeek = :day AND subjectId IN (:subjectIds)")
    fun getBlocksForDay(subjectIds: List<Long>, day: Int): Flow<List<ScheduleBlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: ScheduleBlockEntity): Long

    @Update
    suspend fun updateBlock(block: ScheduleBlockEntity)

    @Delete
    suspend fun deleteBlock(block: ScheduleBlockEntity)

    @Query("DELETE FROM schedule_blocks WHERE subjectId = :subjectId")
    suspend fun deleteBlocksForSubject(subjectId: Long)
}

/**
 * ---------------------------------------------------------
 *                   DAO: Grade Categories
 * ---------------------------------------------------------
 */

@Dao
interface GradesDao {

    /**
     * Categorías (ej: Parciales 30%)
     */
    @Query("SELECT * FROM grade_categories WHERE subjectId = :subjectId")
    fun getCategoriesForSubject(subjectId: Long): Flow<List<GradeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: GradeCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: GradeCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: GradeCategoryEntity)

    @Query("DELETE FROM grade_categories WHERE subjectId = :subjectId")
    suspend fun deleteCategoriesForSubject(subjectId: Long)

    /**
     * Ítems dentro de cada categoría (ej: Parcial 1 = 20%)
     */
    @Query("SELECT * FROM grade_items WHERE categoryId = :categoryId")
    fun getItemsForCategory(categoryId: Long): Flow<List<GradeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GradeItemEntity): Long

    @Update
    suspend fun updateItem(item: GradeItemEntity)

    @Delete
    suspend fun deleteItem(item: GradeItemEntity)

    @Query("DELETE FROM grade_items WHERE categoryId IN (:categoryIds)")
    suspend fun deleteItemsForCategories(categoryIds: List<Long>)
}
