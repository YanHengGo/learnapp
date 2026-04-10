package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.ChildrenRepository
import com.learn.app.core.model.Child
import javax.inject.Inject

class CreateChildUseCase @Inject constructor(
    private val childrenRepository: ChildrenRepository,
) {
    suspend operator fun invoke(name: String, grade: String?): Result<Child> =
        runCatching { childrenRepository.createChild(name, grade) }
}
