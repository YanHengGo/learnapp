package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.TaskRepository
import javax.inject.Inject

class ReorderTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    // orders: taskId と新しい sort_order のペアリスト
    suspend operator fun invoke(childId: String, orders: List<Pair<String, Int>>): Result<Unit> =
        runCatching { taskRepository.reorderTasks(childId, orders) }
}
