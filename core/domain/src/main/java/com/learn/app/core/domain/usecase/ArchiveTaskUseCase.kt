package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.TaskRepository
import javax.inject.Inject

class ArchiveTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String, archived: Boolean): Result<Unit> =
        runCatching {
            taskRepository.patchTask(taskId, mapOf("is_archived" to archived))
        }.map {}
}
