package com.learn.app.core.domain.repository

import com.learn.app.core.model.Child
import kotlinx.coroutines.flow.Flow

interface ChildrenRepository {
    fun getChildren(): Flow<List<Child>>
    suspend fun createChild(name: String, grade: String?): Child
    suspend fun updateChild(childId: String, name: String, grade: String?): Child
    suspend fun deleteChild(childId: String)
}
