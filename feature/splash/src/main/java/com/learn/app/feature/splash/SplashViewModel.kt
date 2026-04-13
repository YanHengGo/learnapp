package com.learn.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.datastore.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {

    sealed interface Destination {
        object Auth : Destination
        object Children : Destination
    }

    private val _destination = MutableStateFlow<Destination?>(null)
    val destination: StateFlow<Destination?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val token = tokenDataStore.token.first()
            _destination.value = if (token != null) Destination.Children else Destination.Auth
        }
    }
}
