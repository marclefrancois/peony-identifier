package com.pivoinescapano.identifier.platform

import androidx.compose.runtime.Composable

/**
 * Cross-platform back button handler
 * - Android: Intercepts physical back button
 * - iOS: No-op (relies on system gesture navigation)
 */
@Composable
expect fun BackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)