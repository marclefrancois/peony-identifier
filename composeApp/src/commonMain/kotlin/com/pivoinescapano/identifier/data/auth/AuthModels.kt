package com.pivoinescapano.identifier.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class GoogleUser(
    val id: String,
    val email: String,
    val name: String,
    val accessToken: String,
)

sealed class AuthResult<T> {
    data class Success<T>(val data: T) : AuthResult<T>()

    data class Error<T>(val message: String, val cause: Throwable? = null) : AuthResult<T>()
}

data class AuthState(
    val user: GoogleUser? = null,
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
)
