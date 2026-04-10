package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.SummaryRepository
import com.learn.app.core.model.CalendarSummary
import javax.inject.Inject

class GetCalendarSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(childId: String, from: String, to: String): Result<CalendarSummary> =
        runCatching { summaryRepository.getCalendarSummary(childId, from, to) }
}
