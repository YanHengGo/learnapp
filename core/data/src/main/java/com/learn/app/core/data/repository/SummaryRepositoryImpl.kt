package com.learn.app.core.data.repository

import com.learn.app.core.data.mapper.toModel
import com.learn.app.core.domain.repository.SummaryRepository
import com.learn.app.core.model.CalendarSummary
import com.learn.app.core.model.Summary
import com.learn.app.core.network.LearnApiService
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
) : SummaryRepository {

    override suspend fun getCalendarSummary(
        childId: String,
        from: String,
        to: String,
    ): CalendarSummary = api.getCalendarSummary(childId, from, to).toModel()

    override suspend fun getSummary(
        childId: String,
        from: String,
        to: String,
    ): Summary = api.getSummary(childId, from, to).toModel()
}
