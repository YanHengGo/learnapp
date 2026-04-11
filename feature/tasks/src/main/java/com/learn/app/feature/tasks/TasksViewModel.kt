package com.learn.app.feature.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.ArchiveTaskUseCase
import com.learn.app.core.domain.usecase.CreateTaskUseCase
import com.learn.app.core.domain.usecase.GetTasksUseCase
import com.learn.app.core.domain.usecase.UpdateTaskUseCase
import com.learn.app.core.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var uiState by mutableStateOf(TasksUiState())
        private set

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            getTasksUseCase(childId)
                .onSuccess { tasks ->
                    uiState = uiState.copy(isLoading = false, tasks = tasks)
                }
                .onFailure {
                    uiState = uiState.copy(isLoading = false, errorMessage = "タスクの取得に失敗しました")
                }
        }
    }

    fun onShowAddDialog() {
        uiState = uiState.copy(
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

    fun onShowEditDialog(task: Task) {
        uiState = uiState.copy(
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

    fun onDismissDialog() {
        uiState = uiState.copy(showDialog = false, editingTask = null)
    }

    fun onNameChange(v: String) { uiState = uiState.copy(dialogName = v) }
    fun onDescriptionChange(v: String) { uiState = uiState.copy(dialogDescription = v) }
    fun onSubjectChange(v: String) { uiState = uiState.copy(dialogSubject = v) }
    fun onMinutesChange(v: String) { uiState = uiState.copy(dialogMinutes = v) }
    fun onDayToggle(dayIndex: Int) {
        uiState = uiState.copy(dialogDaysMask = uiState.dialogDaysMask.toggleDayBit(dayIndex))
    }
    fun onStartDateChange(v: String) { uiState = uiState.copy(dialogStartDate = v) }
    fun onEndDateChange(v: String) { uiState = uiState.copy(dialogEndDate = v) }

    fun onSaveTask() {
        val name = uiState.dialogName.trim()
        val subject = uiState.dialogSubject.trim()
        val minutes = uiState.dialogMinutes.toIntOrNull() ?: return
        if (name.isBlank() || subject.isBlank() || minutes <= 0) return

        val task = Task(
            id = uiState.editingTask?.id ?: "",
            name = name,
            description = uiState.dialogDescription.trim().ifBlank { null },
            subject = subject,
            defaultMinutes = minutes,
            daysMask = uiState.dialogDaysMask,
            isArchived = false,
            startDate = uiState.dialogStartDate.trim().ifBlank { null },
            endDate = uiState.dialogEndDate.trim().ifBlank { null },
        )

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            val editingTask = uiState.editingTask
            if (editingTask != null) {
                updateTaskUseCase(childId, editingTask.id, task)
                    .onSuccess {
                        uiState = uiState.copy(isSaving = false, showDialog = false, editingTask = null)
                        loadTasks()
                    }
                    .onFailure {
                        uiState = uiState.copy(isSaving = false, errorMessage = "更新に失敗しました")
                    }
            } else {
                createTaskUseCase(childId, task)
                    .onSuccess {
                        uiState = uiState.copy(isSaving = false, showDialog = false)
                        loadTasks()
                    }
                    .onFailure {
                        uiState = uiState.copy(isSaving = false, errorMessage = "追加に失敗しました")
                    }
            }
        }
    }

    fun onArchiveTask(task: Task) {
        viewModelScope.launch {
            archiveTaskUseCase(task.id, true)
                .onSuccess { loadTasks() }
                .onFailure { uiState = uiState.copy(errorMessage = "アーカイブに失敗しました") }
        }
    }

    fun onErrorDismiss() {
        uiState = uiState.copy(errorMessage = null)
    }
}
