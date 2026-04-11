package com.learn.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.GetChildrenUseCase
import com.learn.app.core.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChildrenUseCase: GetChildrenUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val childId: String = checkNotNull(savedStateHandle["childId"])

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadChildren()
    }

    private fun loadChildren() {
        viewModelScope.launch {
            getChildrenUseCase()
                .onSuccess { children ->
                    val current = children.find { it.id == childId }
                    uiState = uiState.copy(
                        children = children,
                        selectedChildName = current?.name ?: "",
                    )
                }
        }
    }

    fun onShowSwitcher() { uiState = uiState.copy(showSwitcher = true) }
    fun onDismissSwitcher() { uiState = uiState.copy(showSwitcher = false) }
    fun onTabSelected(tab: HomeTab) { uiState = uiState.copy(selectedTab = tab) }

    fun onShowLogoutConfirm() { uiState = uiState.copy(showLogoutConfirm = true) }
    fun onDismissLogoutConfirm() { uiState = uiState.copy(showLogoutConfirm = false) }

    fun onLogout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onSuccess()
        }
    }
}
