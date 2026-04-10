package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.DailyRepository
import com.learn.app.core.model.DailyView
import javax.inject.Inject

class GetDailyViewUseCase @Inject constructor(
    private val dailyRepository: DailyRepository,
) {
    suspend operator fun invoke(childId: String, date: String): Result<DailyView> =
        runCatching { dailyRepository.getDailyView(childId, date) }
}
