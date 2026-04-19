package com.learn.app.feature.daily

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.common.toErrorMessage
import com.learn.app.core.domain.usecase.GetDailyViewUseCase
import com.learn.app.core.domain.usecase.UpdateDailyLogUseCase
import com.learn.app.core.model.DailyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(DailyUiState())
    val uiState: StateFlow<DailyUiState> = _uiState.asStateFlow()

    init {
        loadDailyView()
    }

    private fun loadDailyView() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
            getDailyViewUseCase(childId, currentDate.toString())
                .onSuccess { view ->
                    _uiState.update { it.copy(
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
                    ) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.toErrorMessage("データの取得に失敗しました")) }
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
        _uiState.update { state ->
            state.copy(
                taskRows = state.taskRows.map { row ->
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
    }

    fun onMinutesChange(taskId: String, value: String) {
        _uiState.update { state ->
            state.copy(
                taskRows = state.taskRows.map { row ->
                    if (row.taskId == taskId) row.copy(minutes = value) else row
                }
            )
        }
    }

    fun onSave() {
        val items = _uiState.value.taskRows
            .filter { it.isDone }
            .map { row ->
                DailyItem(
                    taskId = row.taskId,
                    minutes = row.minutes.toIntOrNull()?.takeIf { it > 0 } ?: row.defaultMinutes,
                )
            }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            updateDailyLogUseCase(childId, currentDate.toString(), items)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = throwable.toErrorMessage("保存に失敗しました")) }
                }
        }
    }

    fun onErrorDismiss() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onSaveSuccessDismiss() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
