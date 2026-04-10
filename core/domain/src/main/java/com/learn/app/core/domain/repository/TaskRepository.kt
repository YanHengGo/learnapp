package com.learn.app.core.domain.repository

import com.learn.app.core.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(childId: String, archived: Boolean = false): Flow<List<Task>>
    suspend fun createTask(childId: String, task: Task): Task
    suspend fun updateTask(childId: String, task: Task): Task
    suspend fun archiveTask(taskId: String, archived: Boolean)
    suspend fun reorderTasks(childId: String, taskIds: List<String>)
}
