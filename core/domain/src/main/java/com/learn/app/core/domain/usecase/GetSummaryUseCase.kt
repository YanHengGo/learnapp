package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.SummaryRepository
import com.learn.app.core.model.Summary
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(childId: String, from: String, to: String): Result<Summary> =
        runCatching { summaryRepository.getSummary(childId, from, to) }
}
