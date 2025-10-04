package com.pivoinescapano.identifier.data.remote

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error(val message: String, val cause: Exception? = null) : NetworkResult<Nothing>()

    data object NetworkUnavailable : NetworkResult<Nothing>()
}
