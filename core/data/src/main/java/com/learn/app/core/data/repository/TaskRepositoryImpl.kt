package com.learn.app.core.data.repository

import com.learn.app.core.data.mapper.toModel
import com.learn.app.core.domain.repository.TaskRepository
import com.learn.app.core.model.Task
import com.learn.app.core.network.LearnApiService
import com.learn.app.core.network.request.CreateTaskRequest
import com.learn.app.core.network.request.ReorderItem
import com.learn.app.core.network.request.ReorderRequest
import com.learn.app.core.network.request.UpdateTaskRequest
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
) : TaskRepository {

    override suspend fun getTasks(childId: String, archived: Boolean): List<Task> =
        api.getTasks(childId, archived).map { it.toModel() }

    override suspend fun createTask(childId: String, task: Task): Task =
        api.createTask(childId, task.toCreateRequest()).toModel()

    override suspend fun updateTask(childId: String, taskId: String, task: Task): Task =
        api.updateTask(childId, taskId, task.toUpdateRequest()).toModel()

    override suspend fun patchTask(taskId: String, fields: Map<String, Any?>): Task =
        api.patchTask(taskId, fields).toModel()

    override suspend fun reorderTasks(childId: String, orders: List<Pair<String, Int>>) {
        val body = ReorderRequest(
            orders = orders.map { (taskId, sortOrder) -> ReorderItem(taskId, sortOrder) },
        )
        api.reorderTasks(childId, body)
    }

    private fun Task.toCreateRequest() = CreateTaskRequest(
        name = name,
        description = description,
        subject = subject,
        defaultMinutes = defaultMinutes,
        daysMask = daysMask,
        startDate = startDate,
        endDate = endDate,
    )

    private fun Task.toUpdateRequest() = UpdateTaskRequest(
        name = name,
        description = description,
        subject = subject,
        defaultMinutes = defaultMinutes,
        daysMask = daysMask,
        isArchived = isArchived,
        startDate = startDate,
        endDate = endDate,
    )
}
