package co.edu.unab.sebastianlizcano.unabgo.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * ---------------------------------------------------------
 *  ENTIDADES PRINCIPALES (Subjects, Schedule, Grades)
 * ---------------------------------------------------------
 */

/**
 * Materia registrada por el usuario
 * - Cada materia tiene color, créditos y nombre.
 * - userId permite que cada estudiante tenga sus propios datos.
 */
@Entity(
    tableName = "subjects",
    indices = [Index(value = ["userId"])]
)
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val name: String,
    val color: Long, // Color ARGB
    val credits: Int
)

/**
 * Bloque horario de una materia
 * dayOfWeek: 1 = lunes ... 7 = domingo
 * startMinutes/endMinutes: minutos desde medianoche (ej: 8:00 → 480)
 */
@Entity(
    tableName = "schedule_blocks",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class ScheduleBlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val dayOfWeek: Int,
    val startMinutes: Int,
    val endMinutes: Int,
    val location: String
)

/**
 * Categoría de notas
 * Ej: "Parciales" → 30% del total final
 */
@Entity(
    tableName = "grade_categories",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class GradeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val name: String,
    val weightInFinal: Float // porcentaje 0–100
)

/**
 * Ítem de nota dentro de una categoría
 * Ej: Parcial 1 = 3.1 (20% dentro de Parciales)
 */
@Entity(
    tableName = "grade_items",
    foreignKeys = [
        ForeignKey(
            entity = GradeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class GradeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val grade: Float,
    val weightInCategory: Float // 0–100 dentro de la categoría
)

/**
 * ---------------------------------------------------------
 *  RELACIONES (Room @Relation)
 * ---------------------------------------------------------
 */

data class SubjectWithSchedule(
    @Embedded val subject: SubjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val schedule: List<ScheduleBlockEntity>
)

data class CategoryWithItems(
    @Embedded val category: GradeCategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val items: List<GradeItemEntity>
)

data class SubjectWithGrades(
    @Embedded val subject: SubjectEntity,
    @Relation(
        entity = GradeCategoryEntity::class,
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val categories: List<CategoryWithItems>
)
