package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmk.kmpauth.google.GoogleAuthUiProvider
import com.pivoinescapano.identifier.data.auth.AuthRepository
import com.pivoinescapano.identifier.data.auth.AuthResult
import com.pivoinescapano.identifier.data.auth.AuthState
import com.pivoinescapano.identifier.data.auth.GoogleUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _authState.value = AuthState(
                user = user,
                isAuthenticated = user != null,
            )
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
            } else {
                _authState.value = _authState.value.copy(isLoading = false)
            }
        } catch (e: Exception) {
            _authState.value = _authState.value.copy(isLoading = false)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState()
        }
    }
}
