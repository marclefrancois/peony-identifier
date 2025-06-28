package com.pivoinescapano.identifier

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.di.appModule
import com.pivoinescapano.identifier.presentation.screen.FieldSelectionScreen
import com.pivoinescapano.identifier.presentation.screen.PeonyIdentifierScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

// v1.3 Navigation State with state persistence
sealed class Screen {
    data class FieldSelection(
        val initialChamp: String? = null,
        val initialParcelle: String? = null
    ) : Screen()
    data class PeonyIdentifier(val champ: String, val parcelle: String) : Screen()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            // v1.3 Fix: Add proper background for iOS
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.FieldSelection()) }
                
                // Animated screen transitions
                val density = LocalDensity.current
                
                AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    // Determine direction based on screen transition
                    val isGoingForward = when {
                        initialState is Screen.FieldSelection && targetState is Screen.PeonyIdentifier -> true
                        initialState is Screen.PeonyIdentifier && targetState is Screen.FieldSelection -> false
                        else -> true
                    }
                    
                    if (isGoingForward) {
                        // Forward: slide in from right, slide out to left
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { with(density) { 300.dp.roundToPx() } }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { with(density) { (-300).dp.roundToPx() } }
                        )
                    } else {
                        // Backward: slide in from left, slide out to right
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { with(density) { (-300).dp.roundToPx() } }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { with(density) { 300.dp.roundToPx() } }
                        )
                    }
                }
            ) { screen ->
                when (screen) {
                    is Screen.FieldSelection -> {
                        FieldSelectionScreen(
                            initialChamp = screen.initialChamp,
                            initialParcelle = screen.initialParcelle,
                            onContinue = { champ, parcelle ->
                                currentScreen = Screen.PeonyIdentifier(champ, parcelle)
                            }
                        )
                    }
                    is Screen.PeonyIdentifier -> {
                        PeonyIdentifierScreen(
                            selectedChamp = screen.champ,
                            selectedParcelle = screen.parcelle,
                            onNavigateBack = {
                                currentScreen = Screen.FieldSelection(
                                    initialChamp = screen.champ,
                                    initialParcelle = screen.parcelle
                                )
                            }
                        )
                    }
                }
            }
            }
        }
    }
}