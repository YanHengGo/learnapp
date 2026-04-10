package com.learn.app.core.domain.repository

import com.learn.app.core.model.Task

interface TaskRepository {
    suspend fun getTasks(childId: String, archived: Boolean = false): List<Task>
    suspend fun createTask(childId: String, task: Task): Task
    suspend fun updateTask(childId: String, taskId: String, task: Task): Task
    suspend fun patchTask(taskId: String, fields: Map<String, Any?>): Task
    suspend fun reorderTasks(childId: String, orders: List<Pair<String, Int>>)
}
