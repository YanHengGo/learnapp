package com.learn.app.feature.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.datastore.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var destination by mutableStateOf<Destination?>(null)
        private set

    init {
        viewModelScope.launch {
            val token = tokenDataStore.token.first()
            destination = if (token != null) Destination.Children else Destination.Auth
        }
    }
}
