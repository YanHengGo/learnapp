package com.learn.app.feature.daily

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.GetDailyViewUseCase
import com.learn.app.core.domain.usecase.UpdateDailyLogUseCase
import com.learn.app.core.model.DailyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDailyViewUseCase: GetDailyViewUseCase,
    private val updateDailyLogUseCase: UpdateDailyLogUseCase,
) : ViewModel() {

    private val childId: String = checkNotNull(savedStateHandle["childId"])
    private var currentDate: LocalDate = run {
        val dateStr = savedStateHandle.get<String>("date")
        if (dateStr.isNullOrBlank()) LocalDate.now() else LocalDate.parse(dateStr)
    }

    var uiState by mutableStateOf(DailyUiState())
        private set

    init {
        loadDailyView()
    }

    private fun loadDailyView() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, saveSuccess = false)
            getDailyViewUseCase(childId, currentDate.toString())
                .onSuccess { view ->
                    uiState = uiState.copy(
                        isLoading = false,
                        date = view.date,
                        weekday = view.weekday,
                        taskRows = view.tasks.map { task ->
                            DailyTaskRow(
                                taskId = task.taskId,
                                name = task.name,
                                subject = task.subject,
                                defaultMinutes = task.defaultMinutes,
                                isDone = task.isDone,
                                minutes = if (task.isDone && task.minutes > 0) task.minutes.toString()
                                          else task.defaultMinutes.toString(),
                            )
                        },
                    )
                }
                .onFailure {
                    uiState = uiState.copy(isLoading = false, errorMessage = "データの取得に失敗しました")
                }
        }
    }

    fun onPreviousDate() {
        currentDate = currentDate.minusDays(1)
        loadDailyView()
    }

    fun onNextDate() {
        currentDate = currentDate.plusDays(1)
        loadDailyView()
    }

    fun onToggleDone(taskId: String) {
        uiState = uiState.copy(
            taskRows = uiState.taskRows.map { row ->
                if (row.taskId == taskId) {
                    val nowDone = !row.isDone
                    row.copy(
                        isDone = nowDone,
                        minutes = if (nowDone) row.defaultMinutes.toString() else "0",
                    )
                } else row
            }
        )
    }

    fun onMinutesChange(taskId: String, value: String) {
        uiState = uiState.copy(
            taskRows = uiState.taskRows.map { row ->
                if (row.taskId == taskId) row.copy(minutes = value) else row
            }
        )
    }

    fun onSave() {
        val items = uiState.taskRows.map { row ->
            DailyItem(
                taskId = row.taskId,
                minutes = if (row.isDone) (row.minutes.toIntOrNull() ?: row.defaultMinutes) else 0,
            )
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            updateDailyLogUseCase(childId, currentDate.toString(), items)
                .onSuccess {
                    uiState = uiState.copy(isSaving = false, saveSuccess = true)
                }
                .onFailure {
                    uiState = uiState.copy(isSaving = false, errorMessage = "保存に失敗しました")
                }
        }
    }

    fun onErrorDismiss() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun onSaveSuccessDismiss() {
        uiState = uiState.copy(saveSuccess = false)
    }
}
