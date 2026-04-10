package com.learn.app.core.data.repository

import com.learn.app.core.data.mapper.toModel
import com.learn.app.core.domain.repository.ChildrenRepository
import com.learn.app.core.model.Child
import com.learn.app.core.network.LearnApiService
import com.learn.app.core.network.request.CreateChildRequest
import com.learn.app.core.network.request.UpdateChildRequest
import javax.inject.Inject

class ChildrenRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
) : ChildrenRepository {

    override suspend fun getChildren(): List<Child> =
        api.getChildren().map { it.toModel() }

    override suspend fun createChild(name: String, grade: String?): Child =
        api.createChild(CreateChildRequest(name = name, grade = grade)).toModel()

    override suspend fun updateChild(childId: String, name: String, grade: String?): Child =
        api.updateChild(childId, UpdateChildRequest(name = name, grade = grade)).toModel()

    override suspend fun patchChild(childId: String, fields: Map<String, Any?>): Child =
        api.patchChild(childId, fields).toModel()

    override suspend fun deleteChild(childId: String) {
        api.deleteChild(childId)
    }
}
