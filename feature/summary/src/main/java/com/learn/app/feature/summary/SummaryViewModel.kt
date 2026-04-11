package com.learn.app.feature.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.GetCalendarSummaryUseCase
import com.learn.app.core.domain.usecase.GetSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var uiState by mutableStateOf(SummaryUiState())
        private set

    init {
        loadMonth(YearMonth.now())
    }

    fun onPreviousMonth() {
        loadMonth(uiState.yearMonth.minusMonths(1))
    }

    fun onNextMonth() {
        loadMonth(uiState.yearMonth.plusMonths(1))
    }

    private fun loadMonth(yearMonth: YearMonth) {
        val from = yearMonth.atDay(1).toString()
        val to = yearMonth.atEndOfMonth().toString()

        uiState = uiState.copy(yearMonth = yearMonth, isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val calendarResult = getCalendarSummaryUseCase(childId, from, to)
            val summaryResult = getSummaryUseCase(childId, from, to)

            val calendar = calendarResult.getOrNull()
            val summary = summaryResult.getOrNull()
            val error = if (calendarResult.isFailure && summaryResult.isFailure) "データの取得に失敗しました" else null

            uiState = uiState.copy(
                isLoading = false,
                calendarSummary = calendar,
                summary = summary,
                errorMessage = error,
            )
        }
    }

    fun onErrorDismiss() {
        uiState = uiState.copy(errorMessage = null)
    }
}
