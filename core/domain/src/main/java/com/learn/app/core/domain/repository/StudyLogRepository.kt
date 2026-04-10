package com.learn.app.core.domain.repository

import com.learn.app.core.model.StudyLog
import kotlinx.coroutines.flow.Flow

interface StudyLogRepository {
    fun getDailyLogs(childId: String, date: String): Flow<List<StudyLog>>
    suspend fun updateDailyLogs(childId: String, date: String, logs: List<StudyLog>)
}
