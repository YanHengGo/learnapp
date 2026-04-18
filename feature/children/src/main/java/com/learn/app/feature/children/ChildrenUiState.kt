package com.learn.app.feature.children

import com.learn.app.core.model.Child

data class ChildrenUiState(
    val children: List<Child> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadError: Boolean = false,
    val errorMessage: String? = null,
    val showAddDialog: Boolean = false,
    val editingChild: Child? = null,
    val dialogName: String = "",
    val dialogGrade: String = "",
    val isSaving: Boolean = false,
    val showLogoutConfirm: Boolean = false,
    val showDeleteAccountConfirm: Boolean = false,
    val deleteAccountError: Boolean = false,
)
