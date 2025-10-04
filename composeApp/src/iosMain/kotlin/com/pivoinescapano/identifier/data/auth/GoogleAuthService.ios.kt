package com.pivoinescapano.identifier.data.auth

actual class GoogleAuthService {
    private var cachedUser: GoogleUser? = null

    actual suspend fun saveUser(user: GoogleUser) {
        cachedUser = user
    }

    actual suspend fun signOut() {
        cachedUser = null
    }

    actual suspend fun getAccessToken(): String? {
        return cachedUser?.accessToken
    }

    actual suspend fun getCurrentUser(): GoogleUser? {
        return cachedUser
    }
}
