package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.platform.BackHandler
import com.pivoinescapano.identifier.presentation.component.content.ErrorContent
import com.pivoinescapano.identifier.presentation.component.content.PositionsListContent
import com.pivoinescapano.identifier.presentation.component.navigation.ListTopBar
import com.pivoinescapano.identifier.presentation.component.navigation.SimpleRowSelectionBar
import com.pivoinescapano.identifier.presentation.theme.AppAnimations
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.OverlayCard
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyIdentifierViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeonyIdentifierScreen(
    selectedChamp: String,
    selectedParcelle: String,
    initialSelectedRang: String? = null,
    initialSelectedTrou: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (champ: String, parcelle: String, rang: String, trou: String) -> Unit,
    onUpdateBackStackState: (champ: String, parcelle: String) -> Unit,
    onUpdateSelectionState: (rang: String?, trou: String?) -> Unit = { _, _ -> },
    viewModel: PeonyIdentifierViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val positionListState = rememberLazyListState()
    var showScrollOverlay by remember { mutableStateOf(false) }
    var currentVisiblePosition by remember { mutableStateOf("") }

    // Initialize with field/parcel selection from navigation
    LaunchedEffect(selectedChamp, selectedParcelle) {
        if (uiState.selectedChamp != selectedChamp) {
            viewModel.onChampSelected(selectedChamp)
        }
        if (uiState.selectedParcelle != selectedParcelle) {
            viewModel.onParcelleSelected(selectedParcelle)
        }
    }

    // Restore selection state when returning from detail screen
    LaunchedEffect(initialSelectedRang, initialSelectedTrou) {
        if (initialSelectedRang != null && uiState.selectedRang != initialSelectedRang) {
            viewModel.onRangSelected(initialSelectedRang)
        }
        if (initialSelectedTrou != null && uiState.selectedTrou != initialSelectedTrou) {
            viewModel.onTrouSelected(initialSelectedTrou)
        }
    }

    // Update back stack state when navigating away (handles iOS gestures)
    DisposableEffect(selectedChamp, selectedParcelle) {
        onDispose {
            onUpdateBackStackState(selectedChamp, selectedParcelle)
        }
    }

    // Remove details view logic since it's now a separate screen

    // Show overlay when scrolling position list
    LaunchedEffect(positionListState.isScrollInProgress) {
        if (positionListState.isScrollInProgress) {
            showScrollOverlay = true
        } else {
            delay(AppAnimations.SCROLL_OVERLAY_DELAY_MS) // Keep overlay visible after scroll stops
            showScrollOverlay = false
        }
    }

    // Handle Android physical back button
    BackHandler(enabled = true) {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            ListTopBar(
                selectedChamp = selectedChamp,
                selectedParcelle = selectedParcelle,
                onNavigateBack = onNavigateBack,
            )
        },
        bottomBar = {
            SimpleRowSelectionBar(
                uiState = uiState,
                onPreviousRow = viewModel::goToPreviousRow,
                onNextRow = viewModel::goToNextRow,
                onReset = viewModel::reset,
            )
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Position list content only (details moved to separate screen)
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ErrorContent(
                            error = uiState.error ?: "Unknown error occurred",
                            onDismiss = viewModel::clearError,
                        )
                    }
                }

                uiState.selectedRang != null -> {
                    PositionsListContent(
                        uiState = uiState,
                        onTrouSelected = { trou ->
                            // Update selected trou state first
                            viewModel.onTrouSelected(trou)
                            // Save current selection state for restoration
                            onUpdateSelectionState(uiState.selectedRang, trou)
                            // Then navigate to detail
                            onNavigateToDetail(selectedChamp, selectedParcelle, uiState.selectedRang!!, trou)
                        },
                        listState = positionListState,
                        onVisiblePositionChanged = { currentVisiblePosition = it },
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Select field, parcel, and row to view positions",
                            style = AppTypography.BodyLarge,
                            textAlign = TextAlign.Center,
                            color = AppColors.OnSurfaceVariant,
                            modifier = Modifier.padding(AppSpacing.M),
                        )
                    }
                }
            }

            // Scroll position overlay
            AnimatedVisibility(
                visible = showScrollOverlay && currentVisiblePosition.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                OverlayCard(
                    modifier = Modifier.size(AppSpacing.OverlayCardSize),
                ) {
                    Text(
                        text = "Position",
                        style = AppTypography.LabelMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = currentVisiblePosition,
                        style = AppTypography.HeadlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
