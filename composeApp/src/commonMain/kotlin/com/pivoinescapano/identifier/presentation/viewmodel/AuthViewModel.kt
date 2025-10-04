package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpauth.google.GoogleAuthUiProvider
import com.pivoinescapano.identifier.data.auth.AuthRepository
import com.pivoinescapano.identifier.data.auth.AuthState
import com.pivoinescapano.identifier.data.auth.GoogleUser
import com.pivoinescapano.identifier.data.cache.DataCacheManager
import com.pivoinescapano.identifier.data.model.LoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val dataCacheManager: DataCacheManager,
) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _dataLoadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val dataLoadingState: StateFlow<LoadingState> = _dataLoadingState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _authState.value =
                AuthState(
                    user = user,
                    isAuthenticated = user != null,
                )

            if (user != null) {
                loadDataAfterAuth()
            }
        }
    }

    suspend fun signInWithProvider(
        googleAuthUiProvider: GoogleAuthUiProvider,
        scopes: List<String>,
    ) {
        _authState.value = _authState.value.copy(isLoading = true)

        try {
            val result =
                googleAuthUiProvider.signIn(
                    filterByAuthorizedAccounts = false,
                    isAutoSelectEnabled = true,
                    scopes = scopes,
                )
            if (result != null) {
                val user =
                    GoogleUser(
                        id = result.idToken,
                        email = result.email ?: "",
                        name = result.displayName,
                        accessToken = result.accessToken ?: "",
                    )
                authRepository.saveUser(user)
                _authState.value =
                    AuthState(
                        user = user,
                        isAuthenticated = true,
                        isLoading = false,
                    )

                loadDataAfterAuth()
            } else {
                _authState.value = _authState.value.copy(isLoading = false)
            }
        } catch (e: Exception) {
            _authState.value = _authState.value.copy(isLoading = false)
        }
    }

    private fun loadDataAfterAuth() {
        viewModelScope.launch {
            _dataLoadingState.value = LoadingState.Loading
            val success = dataCacheManager.preloadAllData()
            _dataLoadingState.value =
                if (success) {
                    LoadingState.Success
                } else {
                    LoadingState.Error("Impossible de charger les données. Veuillez vérifier votre connexion.")
                }
        }
    }

    fun retryDataLoading() {
        loadDataAfterAuth()
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState()
        }
    }
}
