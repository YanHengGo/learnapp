package com.learn.app.core.domain.repository

import com.learn.app.core.model.DailyItem
import com.learn.app.core.model.DailyView

interface DailyRepository {
    suspend fun getDailyView(childId: String, date: String): DailyView
    suspend fun getDailyLog(childId: String, date: String): List<DailyItem>
    suspend fun updateDailyLog(childId: String, date: String, items: List<DailyItem>): Int
}
