package com.learn.app.core.data.repository

import com.learn.app.core.domain.repository.ChildrenRepository
import com.learn.app.core.model.Child
import com.learn.app.core.network.LearnApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChildrenRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
) : ChildrenRepository {

    override fun getChildren(): Flow<List<Child>> = flow {
        val response = api.getChildren()
        emit(response.children.map { Child(it.id, it.name, it.grade, it.isActive) })
    }

    override suspend fun createChild(name: String, grade: String?): Child {
        val response = api.createChild(mapOf("name" to name, "grade" to grade))
        return Child(response.id, response.name, response.grade, response.isActive)
    }

    override suspend fun updateChild(childId: String, name: String, grade: String?): Child {
        val response = api.updateChild(childId, mapOf("name" to name, "grade" to grade))
        return Child(response.id, response.name, response.grade, response.isActive)
    }

    override suspend fun deleteChild(childId: String) {
        api.deleteChild(childId)
    }
}
