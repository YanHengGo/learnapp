package com.learn.app.feature.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.ArchiveTaskUseCase
import com.learn.app.core.domain.usecase.CreateTaskUseCase
import com.learn.app.core.domain.usecase.GetTasksUseCase
import com.learn.app.core.domain.usecase.UpdateTaskUseCase
import com.learn.app.core.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTasksUseCase: GetTasksUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val archiveTaskUseCase: ArchiveTaskUseCase,
) : ViewModel() {

    private val childId: String = checkNotNull(savedStateHandle["childId"])

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getTasksUseCase(childId)
                .onSuccess { tasks ->
                    _uiState.update { it.copy(isLoading = false, tasks = tasks) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "タスクの取得に失敗しました") }
                }
        }
    }

    fun onShowAddDialog() {
        _uiState.update {
            it.copy(
                showDialog = true,
                editingTask = null,
                dialogName = "",
                dialogDescription = "",
                dialogSubject = "",
                dialogMinutes = "30",
                dialogDaysMask = 0b0111110,
                dialogStartDate = "",
                dialogEndDate = "",
            )
        }
    }

    fun onShowEditDialog(task: Task) {
        _uiState.update {
            it.copy(
                showDialog = true,
                editingTask = task,
                dialogName = task.name,
                dialogDescription = task.description ?: "",
                dialogSubject = task.subject,
                dialogMinutes = task.defaultMinutes.toString(),
                dialogDaysMask = task.daysMask,
                dialogStartDate = task.startDate ?: "",
                dialogEndDate = task.endDate ?: "",
            )
        }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showDialog = false, editingTask = null) }
    }

    fun onNameChange(v: String) { _uiState.update { it.copy(dialogName = v) } }
    fun onDescriptionChange(v: String) { _uiState.update { it.copy(dialogDescription = v) } }
    fun onSubjectChange(v: String) { _uiState.update { it.copy(dialogSubject = v) } }
    fun onMinutesChange(v: String) { _uiState.update { it.copy(dialogMinutes = v) } }
    fun onDayToggle(dayIndex: Int) {
        _uiState.update { it.copy(dialogDaysMask = it.dialogDaysMask.toggleDayBit(dayIndex)) }
    }
    fun onStartDateChange(v: String) { _uiState.update { it.copy(dialogStartDate = v) } }
    fun onEndDateChange(v: String) { _uiState.update { it.copy(dialogEndDate = v) } }

    fun onSaveTask() {
        val state = _uiState.value
        val name = state.dialogName.trim()
        val subject = state.dialogSubject.trim()
        val minutes = state.dialogMinutes.toIntOrNull() ?: return
        if (name.isBlank() || subject.isBlank() || minutes <= 0) return

        val task = Task(
            id = state.editingTask?.id ?: "",
            name = name,
            description = state.dialogDescription.trim().ifBlank { null },
            subject = subject,
            defaultMinutes = minutes,
            daysMask = state.dialogDaysMask,
            isArchived = false,
            startDate = state.dialogStartDate.trim().ifBlank { null },
            endDate = state.dialogEndDate.trim().ifBlank { null },
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val editingTask = _uiState.value.editingTask
            if (editingTask != null) {
                updateTaskUseCase(childId, editingTask.id, task)
                    .onSuccess {
                        _uiState.update { it.copy(isSaving = false, showDialog = false, editingTask = null) }
                        loadTasks()
                    }
                    .onFailure {
                        _uiState.update { it.copy(isSaving = false, errorMessage = "更新に失敗しました") }
                    }
            } else {
                createTaskUseCase(childId, task)
                    .onSuccess {
                        _uiState.update { it.copy(isSaving = false, showDialog = false) }
                        loadTasks()
                    }
                    .onFailure {
                        _uiState.update { it.copy(isSaving = false, errorMessage = "追加に失敗しました") }
                    }
            }
        }
    }

    fun onArchiveTask(task: Task) {
        viewModelScope.launch {
            archiveTaskUseCase(task.id, true)
                .onSuccess { loadTasks() }
                .onFailure { _uiState.update { it.copy(errorMessage = "アーカイブに失敗しました") } }
        }
    }

    fun onErrorDismiss() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
