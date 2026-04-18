package com.learn.app.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.common.toErrorMessage
import com.learn.app.core.domain.usecase.GetChildrenUseCase
import com.learn.app.core.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChildrenUseCase: GetChildrenUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val childId: String = checkNotNull(savedStateHandle["childId"])

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadChildren()
    }

    private fun loadChildren() {
        viewModelScope.launch {
            getChildrenUseCase()
                .onSuccess { children ->
                    val current = children.find { it.id == childId }
                    _uiState.update {
                        it.copy(
                            children = children,
                            selectedChildName = current?.name ?: "",
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(errorMessage = throwable.toErrorMessage("データの取得に失敗しました")) }
                }
        }
    }

    fun onErrorDismiss() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onShowSwitcher() { _uiState.update { it.copy(showSwitcher = true) } }
    fun onDismissSwitcher() { _uiState.update { it.copy(showSwitcher = false) } }
    fun onTabSelected(tab: HomeTab) { _uiState.update { it.copy(selectedTab = tab) } }

    fun onShowLogoutConfirm() { _uiState.update { it.copy(showLogoutConfirm = true) } }
    fun onDismissLogoutConfirm() { _uiState.update { it.copy(showLogoutConfirm = false) } }

    fun onLogout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onSuccess()
        }
    }
}
