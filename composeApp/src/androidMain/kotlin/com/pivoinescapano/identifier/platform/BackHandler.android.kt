package com.pivoinescapano.identifier.platform

import androidx.activity.compose.BackHandler as AndroidBackHandler
import androidx.compose.runtime.Composable

/**
 * Android implementation of BackHandler using AndroidX Activity Compose
 */
@Composable
actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    AndroidBackHandler(enabled = enabled, onBack = onBack)
}