package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.platform.BackHandler
import com.pivoinescapano.identifier.presentation.component.content.ErrorContent
import com.pivoinescapano.identifier.presentation.component.content.PeonyDetailsContent
import com.pivoinescapano.identifier.presentation.component.content.PositionsListContent
import com.pivoinescapano.identifier.presentation.component.navigation.DetailsTopBar
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PeonyIdentifierScreen(
    selectedChamp: String? = null,
    selectedParcelle: String? = null,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: PeonyIdentifierViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val positionListState = rememberLazyListState()
    var showScrollOverlay by remember { mutableStateOf(false) }
    var currentVisiblePosition by remember { mutableStateOf("") }

    // v1.3 Initialize with field/parcel selection from navigation
    LaunchedEffect(selectedChamp, selectedParcelle) {
        if (selectedChamp != null && selectedParcelle != null) {
            if (uiState.selectedChamp != selectedChamp) {
                viewModel.onChampSelected(selectedChamp)
            }
            if (uiState.selectedParcelle != selectedParcelle) {
                viewModel.onParcelleSelected(selectedParcelle)
            }
        }
    }

    // Animation states
    val isInDetailsView = uiState.showPeonyDetails
    val density = LocalDensity.current

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
    BackHandler(enabled = isInDetailsView) {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            if (isInDetailsView) {
                DetailsTopBar(
                    fieldEntry = uiState.currentFieldEntry,
                    onBackClick = viewModel::navigateBack,
                )
            } else {
                ListTopBar(
                    selectedChamp = selectedChamp,
                    selectedParcelle = selectedParcelle,
                    onNavigateBack = onNavigateBack,
                )
            }
        },
        bottomBar = {
            // Only show bottom bar when not in details view
            if (!isInDetailsView) {
                SimpleRowSelectionBar(
                    uiState = uiState,
                    onPreviousRow = viewModel::goToPreviousRow,
                    onNextRow = viewModel::goToNextRow,
                    onReset = viewModel::reset,
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Animated content transitions using same approach as App.kt
            AnimatedContent(
                targetState = isInDetailsView,
                transitionSpec = {
                    if (targetState) {
                        // Going to details: slide in from right, slide out to left
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { with(density) { 300.dp.roundToPx() } },
                        ) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { with(density) { (-300).dp.roundToPx() } },
                            )
                    } else {
                        // Going back to list: slide in from left, slide out to right
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { with(density) { (-300).dp.roundToPx() } },
                        ) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { with(density) { 300.dp.roundToPx() } },
                            )
                    }
                },
            ) { showingDetails ->
                if (showingDetails) {
                    // Details view
                    PeonyDetailsContent(
                        peony = uiState.currentPeony,
                        fuzzyMatches = uiState.fuzzyMatches,
                        fieldEntry = uiState.currentFieldEntry,
                        onFuzzyMatchSelected = viewModel::onFuzzyMatchSelected,
                    )
                } else {
                    // List view
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
                                onTrouSelected = viewModel::onTrouSelected,
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
