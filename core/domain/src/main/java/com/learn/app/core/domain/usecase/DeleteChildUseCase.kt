package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.ChildrenRepository
import javax.inject.Inject

class DeleteChildUseCase @Inject constructor(
    private val childrenRepository: ChildrenRepository,
) {
    suspend operator fun invoke(childId: String): Result<Unit> =
        runCatching { childrenRepository.deleteChild(childId) }
}
