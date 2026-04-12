package com.learn.app.feature.children

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.CreateChildUseCase
import com.learn.app.core.domain.usecase.DeleteChildUseCase
import com.learn.app.core.domain.usecase.GetChildrenUseCase
import com.learn.app.core.domain.usecase.LogoutUseCase
import com.learn.app.core.domain.usecase.UpdateChildUseCase
import com.learn.app.core.model.Child
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChildrenViewModel @Inject constructor(
    private val getChildrenUseCase: GetChildrenUseCase,
    private val createChildUseCase: CreateChildUseCase,
    private val updateChildUseCase: UpdateChildUseCase,
    private val deleteChildUseCase: DeleteChildUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(ChildrenUiState())
        private set

    init {
        loadChildren()
    }

    fun loadChildren() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, isLoadError = false, errorMessage = null)
            getChildrenUseCase()
                .onSuccess { children ->
                    uiState = uiState.copy(isLoading = false, isLoadError = false, children = children)
                }
                .onFailure {
                    uiState = uiState.copy(isLoading = false, isLoadError = true, errorMessage = "データの取得に失敗しました")
                }
        }
    }

    fun onShowAddDialog() {
        uiState = uiState.copy(showAddDialog = true, dialogName = "", dialogGrade = "")
    }

    fun onShowEditDialog(child: Child) {
        uiState = uiState.copy(editingChild = child, dialogName = child.name, dialogGrade = child.grade ?: "")
    }

    fun onDismissDialog() {
        uiState = uiState.copy(showAddDialog = false, editingChild = null, dialogName = "", dialogGrade = "")
    }

    fun onDialogNameChange(name: String) {
        uiState = uiState.copy(dialogName = name)
    }

    fun onDialogGradeChange(grade: String) {
        uiState = uiState.copy(dialogGrade = grade)
    }

    fun onSaveChild() {
        val name = uiState.dialogName.trim()
        if (name.isBlank()) return

        val grade = uiState.dialogGrade.trim().ifBlank { null }
        val editingChild = uiState.editingChild

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)

            if (editingChild != null) {
                updateChildUseCase(editingChild.id, name, grade)
                    .onSuccess {
                        uiState = uiState.copy(
                            isSaving = false,
                            showAddDialog = false,
                            editingChild = null,
                            dialogName = "",
                            dialogGrade = "",
                        )
                        loadChildren()
                    }
                    .onFailure { uiState = uiState.copy(isSaving = false, errorMessage = "更新に失敗しました") }
            } else {
                createChildUseCase(name, grade)
                    .onSuccess {
                        uiState = uiState.copy(
                            isSaving = false,
                            showAddDialog = false,
                            editingChild = null,
                            dialogName = "",
                            dialogGrade = "",
                        )
                        loadChildren()
                    }
                    .onFailure { uiState = uiState.copy(isSaving = false, errorMessage = "追加に失敗しました") }
            }
        }
    }

    fun onDeleteChild(child: Child) {
        viewModelScope.launch {
            deleteChildUseCase(child.id)
                .onSuccess { loadChildren() }
                .onFailure { uiState = uiState.copy(errorMessage = "削除に失敗しました") }
        }
    }

    fun onErrorDismiss() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun onShowLogoutConfirm() { uiState = uiState.copy(showLogoutConfirm = true) }
    fun onDismissLogoutConfirm() { uiState = uiState.copy(showLogoutConfirm = false) }

    fun onLogout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onSuccess()
        }
    }
}
