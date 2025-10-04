package com.pivoinescapano.identifier.data.auth

class AuthRepository(
    private val authService: GoogleAuthService,
) {
    suspend fun saveUser(user: GoogleUser) {
        authService.saveUser(user)
    }

    suspend fun signOut() {
        authService.signOut()
    }

    suspend fun getCurrentUser(): GoogleUser? {
        return authService.getCurrentUser()
    }

    suspend fun getAccessToken(): String? {
        return authService.getAccessToken()
    }
}
