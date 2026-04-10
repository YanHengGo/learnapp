package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.DailyRepository
import com.learn.app.core.model.DailyItem
import javax.inject.Inject

class UpdateDailyLogUseCase @Inject constructor(
    private val dailyRepository: DailyRepository,
) {
    suspend operator fun invoke(
        childId: String,
        date: String,
        items: List<DailyItem>,
    ): Result<Int> =
        runCatching { dailyRepository.updateDailyLog(childId, date, items) }
}
