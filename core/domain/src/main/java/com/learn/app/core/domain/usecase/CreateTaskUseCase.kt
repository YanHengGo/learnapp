package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.TaskRepository
import com.learn.app.core.model.Task
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(childId: String, task: Task): Result<Task> =
        runCatching { taskRepository.createTask(childId, task) }
}
