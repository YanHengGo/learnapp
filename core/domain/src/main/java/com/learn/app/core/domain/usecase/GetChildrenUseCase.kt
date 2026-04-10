package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.ChildrenRepository
import com.learn.app.core.model.Child
import javax.inject.Inject

class GetChildrenUseCase @Inject constructor(
    private val childrenRepository: ChildrenRepository,
) {
    suspend operator fun invoke(): Result<List<Child>> =
        runCatching { childrenRepository.getChildren() }
}
