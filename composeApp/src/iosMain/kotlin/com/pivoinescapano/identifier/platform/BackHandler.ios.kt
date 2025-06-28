package com.pivoinescapano.identifier.platform

import androidx.compose.runtime.Composable

/**
 * iOS implementation of BackHandler - no-op since iOS uses system gesture navigation
 * The standard iOS swipe-back gesture will work automatically
 */
@Composable
actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // No-op: iOS handles back navigation through system gestures
    // The swipe-from-left-edge gesture will naturally work
}
