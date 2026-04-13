package com.learn.app.feature.children

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.CreateChildUseCase
import com.learn.app.core.domain.usecase.DeleteChildUseCase
import com.learn.app.core.domain.usecase.GetChildrenUseCase
import com.learn.app.core.domain.usecase.LogoutUseCase
import com.learn.app.core.domain.usecase.UpdateChildUseCase
import com.learn.app.core.model.Child
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(ChildrenUiState())
    val uiState: StateFlow<ChildrenUiState> = _uiState.asStateFlow()

    init {
        loadChildren()
    }

    fun loadChildren() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isLoadError = false, errorMessage = null) }
            getChildrenUseCase()
                .onSuccess { children ->
                    _uiState.update { it.copy(isLoading = false, isLoadError = false, children = children) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, isLoadError = true, errorMessage = "データの取得に失敗しました") }
                }
        }
    }

    fun onShowAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, dialogName = "", dialogGrade = "") }
    }

    fun onShowEditDialog(child: Child) {
        _uiState.update { it.copy(editingChild = child, dialogName = child.name, dialogGrade = child.grade ?: "") }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showAddDialog = false, editingChild = null, dialogName = "", dialogGrade = "") }
    }

    fun onDialogNameChange(name: String) {
        _uiState.update { it.copy(dialogName = name) }
    }

    fun onDialogGradeChange(grade: String) {
        _uiState.update { it.copy(dialogGrade = grade) }
    }

    fun onSaveChild() {
        val name = _uiState.value.dialogName.trim()
        if (name.isBlank()) return

        val grade = _uiState.value.dialogGrade.trim().ifBlank { null }
        val editingChild = _uiState.value.editingChild

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            if (editingChild != null) {
                updateChildUseCase(editingChild.id, name, grade)
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                showAddDialog = false,
                                editingChild = null,
                                dialogName = "",
                                dialogGrade = "",
                            )
                        }
                        loadChildren()
                    }
                    .onFailure { _uiState.update { it.copy(isSaving = false, errorMessage = "更新に失敗しました") } }
            } else {
                createChildUseCase(name, grade)
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                showAddDialog = false,
                                editingChild = null,
                                dialogName = "",
                                dialogGrade = "",
                            )
                        }
                        loadChildren()
                    }
                    .onFailure { _uiState.update { it.copy(isSaving = false, errorMessage = "追加に失敗しました") } }
            }
        }
    }

    fun onDeleteChild(child: Child) {
        viewModelScope.launch {
            deleteChildUseCase(child.id)
                .onSuccess { loadChildren() }
                .onFailure { _uiState.update { it.copy(errorMessage = "削除に失敗しました") } }
        }
    }

    fun onErrorDismiss() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onShowLogoutConfirm() { _uiState.update { it.copy(showLogoutConfirm = true) } }
    fun onDismissLogoutConfirm() { _uiState.update { it.copy(showLogoutConfirm = false) } }

    fun onLogout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onSuccess()
        }
    }
}
