package com.learn.app.feature.summary

import com.learn.app.core.model.CalendarSummary
import com.learn.app.core.model.Summary
import java.time.YearMonth

data class SummaryUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val calendarSummary: CalendarSummary? = null,
    val summary: Summary? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
