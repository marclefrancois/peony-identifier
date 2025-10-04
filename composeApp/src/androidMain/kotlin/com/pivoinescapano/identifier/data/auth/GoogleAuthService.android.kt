package com.pivoinescapano.identifier.data.auth

import android.content.Context

actual class GoogleAuthService(
    private val context: Context,
) {
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
