package com.learn.app.core.domain.repository

import com.learn.app.core.model.Child

interface ChildrenRepository {
    suspend fun getChildren(): List<Child>
    suspend fun createChild(name: String, grade: String?): Child
    suspend fun updateChild(childId: String, name: String, grade: String?): Child
    suspend fun patchChild(childId: String, fields: Map<String, Any?>): Child
    suspend fun deleteChild(childId: String)
}
