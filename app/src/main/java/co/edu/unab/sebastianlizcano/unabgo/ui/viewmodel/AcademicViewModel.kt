package co.edu.unab.sebastianlizcano.unabgo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.sebastianlizcano.unabgo.data.local.*
import co.edu.unab.sebastianlizcano.unabgo.data.repository.AcademicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AcademicUiState(
    val subjects: List<SubjectEntity> = emptyList(),
    val subjectAverages: Map<Long, Float?> = emptyMap(),
    val globalAverage: Float? = null
)

data class SubjectDetailState(
    val subject: SubjectEntity? = null,
    val schedule: List<ScheduleBlockEntity> = emptyList(),
    val categories: List<CategoryWithItems> = emptyList(),
    val average: Float? = null
)

class AcademicViewModel(
    private val repository: AcademicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AcademicUiState())
    val uiState: StateFlow<AcademicUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow(SubjectDetailState())
    val detailState: StateFlow<SubjectDetailState> = _detailState.asStateFlow()

    private var currentUserId: String = ""

    fun setUser(userId: String) {
        currentUserId = userId
        observeSubjects()
    }

    private fun observeSubjects() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            repository.getSubjects(currentUserId).collect { subjects ->
                val averages = mutableMapOf<Long, Float?>()

                for (sub in subjects) {
                    repository.getSubjectWithGrades(sub.id)
                        .firstOrNull()
                        ?.let { rel ->
                            val avg = repository.computeSubjectAverage(rel)
                            averages[sub.id] = avg
                        }
                }

                val global = repository.computeGlobalAverage(subjects, averages)

                _uiState.update {
                    it.copy(
                        subjects = subjects,
                        subjectAverages = averages,
                        globalAverage = global
                    )
                }
            }
        }
    }

    fun loadSubjectDetail(subjectId: Long) {
        viewModelScope.launch {
            combine(
                repository.getSubjectWithGrades(subjectId),
                repository.getSubjectWithSchedule(subjectId)
            ) { grades, schedule ->
                val avg = repository.computeSubjectAverage(grades)
                SubjectDetailState(
                    subject = grades.subject,
                    schedule = schedule.schedule,
                    categories = grades.categories,
                    average = avg
                )
            }.collect { detail ->
                _detailState.value = detail
            }
        }
    }

    /**
     * Crea o actualiza una materia.
     * Devuelve el ID real generado por Room.
     */
    suspend fun saveSubject(
        id: Long?,
        userId: String,
        name: String,
        color: Long,
        credits: Int
    ): Long {
        return if (id == null || id == 0L) {
            val newSubject = SubjectEntity(
                userId = userId,
                name = name,
                color = color,
                credits = credits
            )
            repository.insertSubjectReturningId(newSubject)
        } else {
            val updated = SubjectEntity(
                id = id,
                userId = userId,
                name = name,
                color = color,
                credits = credits
            )
            repository.updateSubject(updated)
            id
        }
    }

    fun addScheduleBlock(
        subjectId: Long,
        day: Int,
        startMinutes: Int,
        endMinutes: Int,
        location: String
    ) {
        viewModelScope.launch {
            val block = ScheduleBlockEntity(
                subjectId = subjectId,
                dayOfWeek = day,
                startMinutes = startMinutes,
                endMinutes = endMinutes,
                location = location
            )
            repository.insertBlock(block)
        }
    }

    fun updateScheduleBlock(block: ScheduleBlockEntity) {
        viewModelScope.launch {
            repository.updateBlock(block)
        }
    }

    fun deleteScheduleBlock(block: ScheduleBlockEntity) {
        viewModelScope.launch {
            repository.deleteBlock(block)
        }
    }

    fun addCategory(subjectId: Long, name: String, weight: Float) {
        viewModelScope.launch {
            val cat = GradeCategoryEntity(
                subjectId = subjectId,
                name = name,
                weightInFinal = weight
            )
            repository.insertCategory(cat)
        }
    }

    fun updateCategory(cat: GradeCategoryEntity) {
        viewModelScope.launch {
            repository.updateCategory(cat)
        }
    }

    fun deleteCategory(cat: GradeCategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(cat)
        }
    }

    fun addItem(categoryId: Long, name: String, grade: Float, weight: Float) {
        viewModelScope.launch {
            val item = GradeItemEntity(
                categoryId = categoryId,
                name = name,
                grade = grade,
                weightInCategory = weight
            )
            repository.insertItem(item)
        }
    }

    fun updateItem(item: GradeItemEntity) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: GradeItemEntity) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun deleteSubject(subjectId: Long) {
        viewModelScope.launch {
            val subject = uiState.value.subjects.firstOrNull { it.id == subjectId } ?: return@launch
            repository.deleteSubject(subject)
        }
    }

    /**
     * Obtiene todos los bloques de todas las materias del usuario.
     */
    fun getAllScheduleBlocks(userId: String): Flow<List<ScheduleBlockEntity>> {
        return repository.getSubjects(userId).flatMapLatest { subjects ->
            val ids = subjects.map { it.id }

            if (ids.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    ids.map { subjectId ->
                        repository.getBlocksForSubject(subjectId)
                    }
                ) { blocksArray: Array<List<ScheduleBlockEntity>> ->
                    blocksArray.flatMap { it }
                }
            }
        }
    }
}
