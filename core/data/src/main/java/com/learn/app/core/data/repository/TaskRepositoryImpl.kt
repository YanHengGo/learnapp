package com.learn.app.core.data.repository

import com.learn.app.core.domain.repository.TaskRepository
import com.learn.app.core.model.Task
import com.learn.app.core.network.LearnApiService
import com.learn.app.core.network.TaskResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
) : TaskRepository {

    override fun getTasks(childId: String, archived: Boolean): Flow<List<Task>> = flow {
        val response = api.getTasks(childId, archived)
        emit(response.tasks.map { it.toModel() })
    }

    override suspend fun createTask(childId: String, task: Task): Task =
        api.createTask(childId, task.toBody()).toModel()

    override suspend fun updateTask(childId: String, task: Task): Task =
        api.updateTask(childId, task.id, task.toBody()).toModel()

    override suspend fun archiveTask(taskId: String, archived: Boolean) {
        api.patchTask(taskId, mapOf("is_archived" to archived))
    }

    override suspend fun reorderTasks(childId: String, taskIds: List<String>) {
        api.updateTask(childId, "reorder", mapOf("task_ids" to taskIds))
    }

    private fun TaskResponse.toModel() = Task(
        id = id,
        childId = childId,
        name = name,
        subject = subject,
        defaultMinutes = defaultMinutes,
        daysMask = daysMask,
        sortOrder = sortOrder,
        isArchived = isArchived,
        startDate = startDate,
        endDate = endDate,
    )

    private fun Task.toBody(): Map<String, Any?> = mapOf(
        "name" to name,
        "subject" to subject,
        "default_minutes" to defaultMinutes,
        "days_mask" to daysMask,
        "start_date" to startDate,
        "end_date" to endDate,
    )
}
