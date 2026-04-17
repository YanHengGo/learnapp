package com.learn.app.feature.summary

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.learn.app.core.model.CalendarDay
import com.learn.app.core.model.CalendarStatus
import com.learn.app.core.model.CalendarSummary
import com.learn.app.core.model.Summary
import java.time.YearMonth

private val previewCalendarSummary = CalendarSummary(
    from = "2026-04-01",
    to = "2026-04-30",
    days = listOf(
        CalendarDay(date = "2026-04-01", status = CalendarStatus.GREEN, total = 2, done = 2),
        CalendarDay(date = "2026-04-07", status = CalendarStatus.YELLOW, total = 2, done = 1),
        CalendarDay(date = "2026-04-14", status = CalendarStatus.RED, total = 2, done = 0),
    ),
)

private val previewSummary = Summary(
    from = "2026-04-01",
    to = "2026-04-30",
    totalMinutes = 195,
    byDay = emptyList(),
    bySubject = emptyList(),
    byTask = emptyList(),
)

@Preview(showBackground = true, name = "ローディング中")
@Composable
private fun PreviewLoading() {
    MaterialTheme {
        SummaryContent(
            uiState = SummaryUiState(isLoading = true),
            onBack = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onDaySelected = {},
            onErrorDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "データなし")
@Composable
private fun PreviewEmpty() {
    MaterialTheme {
        SummaryContent(
            uiState = SummaryUiState(
                yearMonth = YearMonth.of(2026, 4),
            ),
            onBack = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onDaySelected = {},
            onErrorDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "データあり")
@Composable
private fun PreviewWithData() {
    MaterialTheme {
        SummaryContent(
            uiState = SummaryUiState(
                yearMonth = YearMonth.of(2026, 4),
                calendarSummary = previewCalendarSummary,
                summary = previewSummary,
            ),
            onBack = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onDaySelected = {},
            onErrorDismiss = {},
        )
    }
}
