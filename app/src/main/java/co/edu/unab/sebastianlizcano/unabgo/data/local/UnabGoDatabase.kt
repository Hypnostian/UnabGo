package co.edu.unab.sebastianlizcano.unabgo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos de Room que guarda:
 * - Materias
 * - Horarios
 * - Categorías de notas
 * - Ítems de notas
 *
 * Version = 1 (aumentar si se agregan migraciones)
 */

@Database(
    entities = [
        SubjectEntity::class,
        ScheduleBlockEntity::class,
        GradeCategoryEntity::class,
        GradeItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class UnabGoDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun gradesDao(): GradesDao

    companion object {
        @Volatile
        private var INSTANCE: UnabGoDatabase? = null // Singleton

        fun getInstance(context: Context): UnabGoDatabase { // Singleton
            return INSTANCE ?: synchronized(this) { // Thread-safe Singleton (Double-Check Locking)
                val instance = Room.databaseBuilder( // Builder Pattern
                    context.applicationContext,
                    UnabGoDatabase::class.java,
                    "unab_go_database"
                )
                    .fallbackToDestructiveMigration() // Cambiar por migraciones reales después
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
