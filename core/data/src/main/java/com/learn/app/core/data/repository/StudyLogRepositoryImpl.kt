package com.learn.app.core.data.repository

import com.learn.app.core.domain.repository.StudyLogRepository
import com.learn.app.core.model.StudyLog
import com.learn.app.core.network.LearnApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StudyLogRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
) : StudyLogRepository {

    override fun getDailyLogs(childId: String, date: String): Flow<List<StudyLog>> = flow {
        val response = api.getDailyLogs(childId, date)
        emit(response.logs.map { StudyLog(it.id, it.childId, it.taskId, it.date, it.minutes) })
    }

    override suspend fun updateDailyLogs(childId: String, date: String, logs: List<StudyLog>) {
        val body = mapOf("logs" to logs.map { mapOf("task_id" to it.taskId, "minutes" to it.minutes) })
        api.updateDailyLogs(childId, date, body)
    }
}
