package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.presentation.component.PeonyAsyncImage
import com.pivoinescapano.identifier.presentation.state.PeonyIdentifierState
import com.pivoinescapano.identifier.presentation.theme.*
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyIdentifierViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeonyIdentifierScreen(
    viewModel: PeonyIdentifierViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val positionListState = rememberLazyListState()
    var showScrollOverlay by remember { mutableStateOf(false) }
    var currentVisiblePosition by remember { mutableStateOf("") }
    
    // Show overlay when scrolling position list
    LaunchedEffect(positionListState.isScrollInProgress) {
        if (positionListState.isScrollInProgress) {
            showScrollOverlay = true
        } else {
            delay(AppAnimations.SCROLL_OVERLAY_DELAY_MS) // Keep overlay visible after scroll stops
            showScrollOverlay = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content area - positions list or peony details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.error != null -> {
                        ErrorContent(
                            error = uiState.error ?: "Unknown error occurred",
                            onDismiss = viewModel::clearError,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.selectedRang != null && uiState.selectedTrou == null -> {
                        // Show positions list for selected row
                        PositionsListContent(
                            uiState = uiState,
                            onTrouSelected = viewModel::onTrouSelected,
                            listState = positionListState,
                            onVisiblePositionChanged = { currentVisiblePosition = it }
                        )
                    }
                    uiState.showPeonyDetails -> {
                        PeonyDetailsContent(
                            peony = uiState.currentPeony,
                            fuzzyMatches = uiState.fuzzyMatches,
                            fieldEntry = uiState.currentFieldEntry,
                            onFuzzyMatchSelected = viewModel::onFuzzyMatchSelected
                        )
                    }
                    else -> {
                        Text(
                            text = "Select field, parcel, and row to view positions",
                            style = AppTypography.BodyLarge,
                            textAlign = TextAlign.Center,
                            color = AppColors.OnSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(AppSpacing.M)
                        )
                    }
                }
            }
            
            // Bottom selection bar (Field, Parcel, Row only)
            BottomSelectionBar(
                uiState = uiState,
                onChampSelected = viewModel::onChampSelected,
                onParcelleSelected = viewModel::onParcelleSelected,
                onRangSelected = viewModel::onRangSelected,
                onReset = viewModel::reset
            )
        }
        
        // Scroll position overlay
        AnimatedVisibility(
            visible = showScrollOverlay && currentVisiblePosition.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            OverlayCard(
                modifier = Modifier.size(AppSpacing.OverlayCardSize)
            ) {
                Text(
                    text = "Position",
                    style = AppTypography.LabelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = currentVisiblePosition,
                    style = AppTypography.HeadlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    UniformCard(
        modifier = modifier.padding(AppSpacing.M),
        backgroundColor = AppColors.Error.copy(alpha = 0.1f),
        contentColor = AppColors.Error
    ) {
        Text(
            text = "Error",
            style = AppTypography.HeadlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = error,
            style = AppTypography.BodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterHorizontally)
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
    onFuzzyMatchSelected: (PeonyInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = AppSpacing.M,
            vertical = AppSpacing.M
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
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
                    color = AppColors.OnSurface
                )
            }
            items(fuzzyMatches) { match ->
                PeonyCard(
                    peony = match,
                    isExactMatch = false,
                    onClick = { onFuzzyMatchSelected(match) }
                )
            }
        }
    }
}

@Composable
private fun FieldEntryCard(entry: FieldEntry) {
    UniformCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Variety: ${entry.variete ?: "Unknown"}",
            style = AppTypography.HeadlineSmall,
            color = AppColors.OnSurface
        )
        
        // Only show additional info if available
        val additionalInfo = buildList {
            entry.annee_plantation?.let { add("Planted: $it") }
            entry.taille?.let { add("Size: $it") }
        }
        
        if (additionalInfo.isNotEmpty()) {
            additionalInfo.forEach { info ->
                Text(
                    text = info,
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PeonyCard(
    peony: PeonyInfo,
    isExactMatch: Boolean,
    onClick: (() -> Unit)? = null
) {
    UniformCard(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it },
        backgroundColor = if (isExactMatch) AppColors.ExactMatch.copy(alpha = 0.1f) else AppColors.SurfaceContainer,
        contentColor = AppColors.OnSurface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = peony.cultivar,
                style = AppTypography.HeadlineSmall,
                color = AppColors.OnSurface,
                modifier = Modifier.weight(1f)
            )
            if (isExactMatch) {
                Badge(
                    containerColor = AppColors.ExactMatch,
                    contentColor = AppColors.OnPrimary
                ) {
                    Text(
                        text = "Exact Match",
                        style = AppTypography.LabelSmall
                    )
                }
            }
        }
        
        // Image and info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
        ) {
            // Peony image
            PeonyAsyncImage(
                imageUrl = peony.image,
                contentDescription = "Image of ${peony.cultivar}",
                modifier = Modifier.size(120.dp)
            )
            
            // Peony details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.XS)
            ) {
                Text(
                    text = "Originator: ${peony.originator}",
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant
                )
                Text(
                    text = "Date: ${peony.date}",
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant
                )
                Text(
                    text = "Group: ${peony.group}",
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }
        
        if (peony.description.isNotBlank()) {
            Text(
                text = "Description:",
                style = AppTypography.LabelLarge,
                color = AppColors.OnSurface
            )
            Text(
                text = peony.description.replace(Regex("<[^>]*>"), ""), // Strip HTML tags
                style = AppTypography.BodyMedium,
                color = AppColors.OnSurfaceVariant
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
    onVisiblePositionChanged: (String) -> Unit
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
        contentPadding = PaddingValues(
            horizontal = AppSpacing.M,
            vertical = AppSpacing.M
        ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        items(positions) { position ->
            val entry = fieldEntries.find { it.trou == position }
            PositionCard(
                position = position,
                entry = entry,
                onClick = { onTrouSelected(position) }
            )
        }
    }
}

@Composable
private fun PositionCard(
    position: String,
    entry: FieldEntry?,
    onClick: () -> Unit
) {
    UniformCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 2.dp,
        backgroundColor = AppColors.SurfaceContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Position $position",
                    style = AppTypography.LabelLarge,
                    color = AppColors.PrimaryPurple
                )
                entry?.variete?.let { variety ->
                    Text(
                        text = variety,
                        style = AppTypography.BodyLarge,
                        color = AppColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                entry?.taille?.let { size ->
                    Text(
                        text = "Size: $size",
                        style = AppTypography.BodySmall,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }
            Text(
                text = "→",
                style = AppTypography.LabelLarge,
                color = AppColors.PrimaryPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Value-only dropdown for the bottom bar (no internal labels)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ValueOnlyDropdown(
    selectedValue: String?,
    options: List<String>,
    onSelectionChanged: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue ?: "--",
            onValueChange = {},
            readOnly = true,
            placeholder = { 
                Text(
                    text = "--",
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            trailingIcon = { 
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    modifier = Modifier.size(16.dp)
                ) 
            },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = enabled),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.PrimaryPurple,
                unfocusedBorderColor = AppColors.OnSurfaceVariant.copy(alpha = 0.4f),
                disabledBorderColor = AppColors.OnSurfaceVariant.copy(alpha = 0.2f),
                focusedTextColor = AppColors.OnSurface,
                unfocusedTextColor = AppColors.OnSurface,
                disabledTextColor = AppColors.OnSurfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(AppSpacing.XS),
            textStyle = AppTypography.BodyMedium.copy(textAlign = TextAlign.Center),
            singleLine = true
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = option,
                            style = AppTypography.BodyMedium,
                            color = AppColors.OnSurface
                        ) 
                    },
                    onClick = {
                        onSelectionChanged(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Bottom selection bar for Field, Parcel, Row with external labels
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BottomSelectionBar(
    uiState: PeonyIdentifierState,
    onChampSelected: (String) -> Unit,
    onParcelleSelected: (String) -> Unit,
    onRangSelected: (String) -> Unit,
    onReset: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .combinedClickable(
                onLongClick = onReset,
                onClick = {}
            ),
        color = AppColors.SurfaceContainerHigh,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.M, vertical = AppSpacing.S)
        ) {
            // External labels row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
            ) {
                Text(
                    text = "Field",
                    style = AppTypography.LabelMedium,
                    color = AppColors.OnSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.4f)
                )
                Text(
                    text = "Parcel",
                    style = AppTypography.LabelMedium,
                    color = AppColors.OnSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.35f)
                )
                Text(
                    text = "Row",
                    style = AppTypography.LabelMedium,
                    color = AppColors.OnSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.25f)
                )
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.XS))
            
            // Dropdowns row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
            ) {
                ValueOnlyDropdown(
                    selectedValue = uiState.selectedChamp,
                    options = uiState.availableChamps,
                    onSelectionChanged = onChampSelected,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(0.4f)
                )
                
                ValueOnlyDropdown(
                    selectedValue = uiState.selectedParcelle,
                    options = uiState.availableParcelles,
                    onSelectionChanged = onParcelleSelected,
                    enabled = !uiState.isLoading && uiState.selectedChamp != null,
                    modifier = Modifier.weight(0.35f)
                )
                
                ValueOnlyDropdown(
                    selectedValue = uiState.selectedRang,
                    options = uiState.availableRangs,
                    onSelectionChanged = onRangSelected,
                    enabled = !uiState.isLoading && uiState.selectedParcelle != null,
                    modifier = Modifier.weight(0.25f)
                )
            }
        }
    }
}