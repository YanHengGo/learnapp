package com.learn.app.feature.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.GetCalendarSummaryUseCase
import com.learn.app.core.domain.usecase.GetSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCalendarSummaryUseCase: GetCalendarSummaryUseCase,
    private val getSummaryUseCase: GetSummaryUseCase,
) : ViewModel() {

    private val childId: String = checkNotNull(savedStateHandle["childId"])

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        loadMonth(YearMonth.now())
    }

    fun onPreviousMonth() {
        loadMonth(_uiState.value.yearMonth.minusMonths(1))
    }

    fun onNextMonth() {
        loadMonth(_uiState.value.yearMonth.plusMonths(1))
    }

    private fun loadMonth(yearMonth: YearMonth) {
        val from = yearMonth.atDay(1).toString()
        val to = yearMonth.atEndOfMonth().toString()

        _uiState.update { it.copy(yearMonth = yearMonth, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val calendarResult = getCalendarSummaryUseCase(childId, from, to)
            val summaryResult = getSummaryUseCase(childId, from, to)

            val calendar = calendarResult.getOrNull()
            val summary = summaryResult.getOrNull()
            val error = if (calendarResult.isFailure && summaryResult.isFailure) "データの取得に失敗しました" else null

            _uiState.update {
                it.copy(
                    isLoading = false,
                    calendarSummary = calendar,
                    summary = summary,
                    errorMessage = error,
                )
            }
        }
    }

    fun onErrorDismiss() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
