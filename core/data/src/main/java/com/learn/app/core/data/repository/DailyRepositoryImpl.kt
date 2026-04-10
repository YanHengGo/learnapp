package com.learn.app.core.data.repository

import com.learn.app.core.data.mapper.toItems
import com.learn.app.core.data.mapper.toModel
import com.learn.app.core.domain.repository.DailyRepository
import com.learn.app.core.model.DailyItem
import com.learn.app.core.model.DailyView
import com.learn.app.core.network.LearnApiService
import com.learn.app.core.network.request.DailyItemRequest
import com.learn.app.core.network.request.UpdateDailyRequest
import javax.inject.Inject

class DailyRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
) : DailyRepository {

    override suspend fun getDailyView(childId: String, date: String): DailyView =
        api.getDailyView(childId, date).toModel()

    override suspend fun getDailyLog(childId: String, date: String): List<DailyItem> =
        api.getDailyLog(childId, date).toItems()

    override suspend fun updateDailyLog(
        childId: String,
        date: String,
        items: List<DailyItem>,
    ): Int {
        val body = UpdateDailyRequest(
            items = items.map { DailyItemRequest(taskId = it.taskId, minutes = it.minutes) },
        )
        return api.updateDailyLog(childId, date, body).savedCount
    }
}
