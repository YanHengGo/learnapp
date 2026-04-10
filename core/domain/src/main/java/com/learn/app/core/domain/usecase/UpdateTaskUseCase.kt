package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.TaskRepository
import com.learn.app.core.model.Task
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(childId: String, taskId: String, task: Task): Result<Task> =
        runCatching { taskRepository.updateTask(childId, taskId, task) }
}
