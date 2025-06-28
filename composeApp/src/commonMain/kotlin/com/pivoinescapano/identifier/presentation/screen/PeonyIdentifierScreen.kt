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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.platform.BackHandler
import com.pivoinescapano.identifier.presentation.component.PeonyAsyncImage
import com.pivoinescapano.identifier.presentation.state.PeonyIdentifierState
import com.pivoinescapano.identifier.presentation.theme.AppAnimations
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.EnhancedBottomNavigationBar
import com.pivoinescapano.identifier.presentation.theme.OverlayCard
import com.pivoinescapano.identifier.presentation.theme.UniformCard
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

@Composable
private fun ErrorContent(
    error: String,
    onDismiss: () -> Unit,
) {
    UniformCard(
        modifier = Modifier.padding(AppSpacing.M),
        backgroundColor = AppColors.Error.copy(alpha = 0.1f),
        contentColor = AppColors.Error,
    ) {
        Text(
            text = "Error",
            style = AppTypography.HeadlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = error,
            style = AppTypography.BodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Dismiss")
        }
    }
}

@Composable
private fun PeonyDetailsContent(
    peony: PeonyInfo?,
    fuzzyMatches: List<PeonyInfo>,
    fieldEntry: FieldEntry?,
    onFuzzyMatchSelected: (PeonyInfo) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                horizontal = AppSpacing.M,
                vertical = AppSpacing.M,
            ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
    ) {
        // Field entry info
        fieldEntry?.let { entry ->
            item {
                FieldEntryCard(entry)
            }
        }

        // Exact peony match
        peony?.let { p ->
            item {
                PeonyCard(p, isExactMatch = true)
            }
        }

        // Fuzzy matches
        if (fuzzyMatches.isNotEmpty()) {
            item {
                Text(
                    text = if (peony == null) "Possible matches:" else "Other similar varieties:",
                    style = AppTypography.HeadlineSmall,
                    color = AppColors.OnSurface,
                )
            }
            items(fuzzyMatches) { match ->
                PeonyCard(
                    peony = match,
                    isExactMatch = false,
                    onClick = { onFuzzyMatchSelected(match) },
                )
            }
        }
    }
}

@Composable
private fun FieldEntryCard(entry: FieldEntry) {
    UniformCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Variety: ${entry.variete ?: "Unknown"}",
            style = AppTypography.HeadlineSmall,
            color = AppColors.OnSurface,
        )

        // Only show additional info if available
        val additionalInfo =
            buildList {
                entry.annee_plantation?.let { add("Planted: $it") }
                entry.taille?.let { add("Size: $it") }
            }

        if (additionalInfo.isNotEmpty()) {
            additionalInfo.forEach { info ->
                Text(
                    text = info,
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PeonyCard(
    peony: PeonyInfo,
    isExactMatch: Boolean,
    onClick: (() -> Unit)? = null,
) {
    UniformCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .let { if (onClick != null) it.clickable { onClick() } else it },
        backgroundColor = if (isExactMatch) AppColors.ExactMatch.copy(alpha = 0.1f) else AppColors.SurfaceContainer,
        contentColor = AppColors.OnSurface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = peony.cultivar,
                style = AppTypography.HeadlineSmall,
                color = AppColors.OnSurface,
                modifier = Modifier.weight(1f),
            )
            if (isExactMatch) {
                Badge(
                    containerColor = AppColors.ExactMatch,
                    contentColor = AppColors.OnPrimary,
                ) {
                    Text(
                        text = "Exact Match",
                        style = AppTypography.LabelSmall,
                    )
                }
            }
        }

        // Image and info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
        ) {
            // Peony image
            PeonyAsyncImage(
                imageUrl = peony.image,
                contentDescription = "Image of ${peony.cultivar}",
                modifier = Modifier.size(120.dp),
            )

            // Peony details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.XS),
            ) {
                Text(
                    text = "Originator: ${peony.originator}",
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant,
                )
                Text(
                    text = "Date: ${peony.date}",
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant,
                )
                Text(
                    text = "Group: ${peony.group}",
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant,
                )
            }
        }

        if (peony.description.isNotBlank()) {
            Text(
                text = "Description:",
                style = AppTypography.LabelLarge,
                color = AppColors.OnSurface,
            )
            Text(
                text = peony.description.replace(Regex("<[^>]*>"), ""),
                style = AppTypography.BodyMedium,
                color = AppColors.OnSurfaceVariant,
            )
        }
    }
}

// New positions list content
@Composable
private fun PositionsListContent(
    uiState: PeonyIdentifierState,
    onTrouSelected: (String) -> Unit,
    listState: LazyListState,
    onVisiblePositionChanged: (String) -> Unit,
) {
    val positions = uiState.availableTrous
    val fieldEntries = uiState.currentRowEntries // We need to add this to state

    LaunchedEffect(listState.firstVisibleItemIndex, positions) {
        if (positions.isNotEmpty() && listState.firstVisibleItemIndex < positions.size) {
            onVisiblePositionChanged(positions[listState.firstVisibleItemIndex])
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                horizontal = AppSpacing.M,
                vertical = AppSpacing.M,
            ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
    ) {
        items(positions) { position ->
            val entry = fieldEntries.find { it.trou == position }
            PositionCard(
                position = position,
                entry = entry,
                onClick = { onTrouSelected(position) },
            )
        }
    }
}

@Composable
private fun PositionCard(
    position: String,
    entry: FieldEntry?,
    onClick: () -> Unit,
) {
    UniformCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        elevation = 2.dp,
        backgroundColor = AppColors.SurfaceContainer,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Position $position",
                    style = AppTypography.LabelLarge,
                    color = AppColors.PrimaryGreen,
                )
                entry?.variete?.let { variety ->
                    Text(
                        text = variety,
                        style = AppTypography.BodyLarge,
                        color = AppColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                entry?.taille?.let { size ->
                    Text(
                        text = "Size: $size",
                        style = AppTypography.BodySmall,
                        color = AppColors.OnSurfaceVariant,
                    )
                }
            }
            Text(
                text = "→",
                style = AppTypography.LabelLarge,
                color = AppColors.PrimaryGreen,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// v1.4 Simple Row Selection Bar with Arrow Navigation
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SimpleRowSelectionBar(
    uiState: PeonyIdentifierState,
    onPreviousRow: () -> Unit,
    onNextRow: () -> Unit,
    onReset: () -> Unit,
) {
    EnhancedBottomNavigationBar(
        modifier =
            Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .combinedClickable(
                    onLongClick = onReset,
                    onClick = {},
                ),
    ) {
        val selectedRang = uiState.selectedRang
        val availableRangs = uiState.availableRangs
        val isEnabled = !uiState.isLoading && uiState.selectedParcelle != null && availableRangs.isNotEmpty()

        if (isEnabled && selectedRang != null) {
            val currentIndex = availableRangs.indexOf(selectedRang)

            SimpleRowControl(
                currentRow = selectedRang,
                availableRows = availableRangs,
                onPreviousRow = onPreviousRow,
                onNextRow = onNextRow,
            )
        } else {
            // Show placeholder when no row is selected
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text =
                        if (uiState.selectedParcelle == null) {
                            "Select field and parcel first"
                        } else {
                            "Loading rows..."
                        },
                    style = AppTypography.BottomBarLarge,
                    color = AppColors.OnSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}

// v1.4 Simple Row Control with Arrow Navigation
@Composable
private fun SimpleRowControl(
    currentRow: String,
    availableRows: List<String>,
    onPreviousRow: () -> Unit,
    onNextRow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.M),
        ) {
            val currentIndex = availableRows.indexOf(currentRow)
            val canGoToPrevious = currentIndex > 0
            val canGoToNext = currentIndex >= 0 && currentIndex < availableRows.size - 1

            // Previous arrow
            IconButton(
                onClick = onPreviousRow,
                enabled = canGoToPrevious,
                modifier = Modifier.padding(AppSpacing.XS),
            ) {
                Text(
                    text = "‹",
                    style = AppTypography.HeadlineLarge,
                    color =
                        if (canGoToPrevious) {
                            AppColors.PrimaryGreen
                        } else {
                            AppColors.OnSurfaceVariant.copy(
                                alpha = 0.3f,
                            )
                        },
                )
            }

            // Current row display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Row $currentRow",
                    style = AppTypography.BottomBarLarge,
                    color = AppColors.OnSurface,
                )

                // Page indicators
                val totalRows = availableRows.size
                if (totalRows > 1) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp),
                    ) {
                        repeat(minOf(totalRows, 10)) { index -> // Limit to 10 dots
                            val isActive = index == currentIndex
                            Box(
                                modifier =
                                    Modifier
                                        .size(if (isActive) 8.dp else 6.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (isActive) {
                                                AppColors.OnSurface
                                            } else {
                                                AppColors.OnSurfaceVariant.copy(alpha = 0.3f)
                                            },
                                        ),
                            )
                        }

                        // Show "..." if there are more than 10 rows
                        if (totalRows > 10) {
                            Text(
                                text = "...",
                                style = AppTypography.BodySmall,
                                color = AppColors.OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 2.dp),
                            )
                        }
                    }
                }
            }

            // Next arrow
            IconButton(
                onClick = onNextRow,
                enabled = canGoToNext,
                modifier = Modifier.padding(AppSpacing.XS),
            ) {
                Text(
                    text = "›",
                    style = AppTypography.HeadlineLarge,
                    color = if (canGoToNext) AppColors.PrimaryGreen else AppColors.OnSurfaceVariant.copy(alpha = 0.3f),
                )
            }
        }
    }
}

// v1.3 Enhanced Top bar for list view with field info and back navigation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListTopBar(
    selectedChamp: String? = null,
    selectedParcelle: String? = null,
    onNavigateBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            if (selectedChamp != null && selectedParcelle != null) {
                Column {
                    Text(
                        text = "Position Selection",
                        style = AppTypography.HeadlineSmall,
                        color = AppColors.OnSurface,
                    )
                    Text(
                        text = "Field $selectedChamp, Parcel $selectedParcelle",
                        style = AppTypography.BodyMedium,
                        color = AppColors.OnSurfaceVariant,
                    )
                }
            } else {
                Text(
                    text = "Peony Finder",
                    style = AppTypography.HeadlineSmall,
                    color = AppColors.OnSurface,
                )
            }
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to field selection",
                        tint = AppColors.OnSurface,
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = AppColors.SurfaceContainer,
                titleContentColor = AppColors.OnSurface,
                navigationIconContentColor = AppColors.OnSurface,
            ),
    )
}

// Top bar for details view with back button and field info
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsTopBar(
    fieldEntry: FieldEntry?,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Position ${fieldEntry?.trou ?: ""}",
                    style = AppTypography.HeadlineSmall,
                    color = AppColors.OnSurface,
                )
                fieldEntry?.let { entry ->
                    Text(
                        text = "Field ${entry.champ} • Parcel ${entry.parcelle} • Row ${entry.rang}",
                        style = AppTypography.BodySmall,
                        color = AppColors.OnSurfaceVariant,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Text(
                    text = "←",
                    style = AppTypography.HeadlineMedium,
                    color = AppColors.OnSurface,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = AppColors.SurfaceContainer,
                titleContentColor = AppColors.OnSurface,
                navigationIconContentColor = AppColors.OnSurface,
            ),
    )
}
