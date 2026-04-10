package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.TaskRepository
import com.learn.app.core.model.Task
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(childId: String, archived: Boolean = false): Result<List<Task>> =
        runCatching { taskRepository.getTasks(childId, archived) }
}
