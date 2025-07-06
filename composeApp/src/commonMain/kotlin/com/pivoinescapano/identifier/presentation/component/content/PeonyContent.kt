package com.pivoinescapano.identifier.presentation.component.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.platform.currentTimeMillis
import com.pivoinescapano.identifier.presentation.component.cards.FieldEntryCard
import com.pivoinescapano.identifier.presentation.component.cards.PeonyCard
import com.pivoinescapano.identifier.presentation.component.cards.PositionCard
import com.pivoinescapano.identifier.presentation.state.PeonyIdentifierState
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import kotlinx.coroutines.delay

@Composable
fun PeonyDetailsContent(
    peony: PeonyInfo?,
    fuzzyMatches: List<PeonyInfo>,
    fieldEntry: FieldEntry?,
    fieldNote: FieldNote?,
    isNoteSaving: Boolean,
    isPeonyConfirmed: Boolean = false,
    onFuzzyMatchSelected: (PeonyInfo) -> Unit,
    onUpdateFieldNote: (String) -> Unit,
    onUpdateFieldNoteStatus: (FieldNoteStatus) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    focusManager.clearFocus()
                },
        contentPadding =
            PaddingValues(
                horizontal = AppSpacing.M,
                vertical = AppSpacing.M,
            ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
    ) {
        // Field entry info with visual status indicators
        fieldEntry?.let { entry ->
            item {
                FieldEntryCardWithStatus(entry, fieldNote)
            }
        }

        // Quick action buttons
        item {
            QuickActionButtons(
                currentStatus = fieldNote?.status ?: FieldNoteStatus.NORMAL,
                isLoading = isNoteSaving,
                onStatusSelected = onUpdateFieldNoteStatus,
            )
        }

        // Field notes section
        item {
            FieldNotesSection(
                fieldNote = fieldNote,
                isNoteSaving = isNoteSaving,
                onUpdateFieldNote = onUpdateFieldNote,
                focusManager = focusManager,
            )
        }

        // Current peony (exact match or confirmed selection)
        peony?.let { p ->
            item {
                PeonyCard(
                    peony = p,
                    isExactMatch = !isPeonyConfirmed,
                    isConfirmed = isPeonyConfirmed,
                )
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
fun PositionsListContent(
    uiState: PeonyIdentifierState,
    onTrouSelected: (String) -> Unit,
    listState: LazyListState,
    onVisiblePositionChanged: (String) -> Unit,
) {
    val positions = uiState.availableTrous
    val fieldEntries = uiState.currentRowEntries
    val selectedTrou = uiState.selectedTrou

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
        verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
    ) {
        items(positions) { position ->
            val entry = fieldEntries.find { it.trou == position }
            val fieldNote = uiState.rowFieldNotes.find { it.trou == position }
            PositionCard(
                position = position,
                entry = entry,
                fieldNote = fieldNote,
                isSelected = position == selectedTrou,
                onClick = { onTrouSelected(position) },
            )
        }
    }
}

@Composable
fun FieldEntryCardWithStatus(
    entry: FieldEntry,
    fieldNote: FieldNote?,
) {
    val borderColor =
        when (fieldNote?.status) {
            FieldNoteStatus.DEAD -> AppColors.Error
            FieldNoteStatus.BLOCKED -> AppColors.Warning
            else -> if (fieldNote?.notes?.isNotEmpty() == true) AppColors.PrimaryGreen else MaterialTheme.colorScheme.outline
        }

    val borderWidth = if (fieldNote != null) 2.dp else 1.dp

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(borderWidth, borderColor),
                    RoundedCornerShape(12.dp),
                ),
        colors =
            CardDefaults.cardColors(
                containerColor = AppColors.SurfaceContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.M),
        ) {
            FieldEntryCard(entry)

            // Status indicator
            fieldNote?.let { note ->
                Spacer(modifier = Modifier.height(AppSpacing.S))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.XS),
                ) {
                    when (note.status) {
                        FieldNoteStatus.DEAD -> {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Dead plant",
                                tint = AppColors.Error,
                            )
                            Text("Dead plant", style = AppTypography.BodySmall, color = AppColors.Error)
                        }
                        FieldNoteStatus.BLOCKED -> {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = "Position blocked",
                                tint = AppColors.Warning,
                            )
                            Text("Position blocked", style = AppTypography.BodySmall, color = AppColors.Warning)
                        }
                        FieldNoteStatus.NORMAL -> {
                            if (note.notes.isNotEmpty()) {
                                Icon(
                                    Icons.Default.Notes,
                                    contentDescription = "Has notes",
                                    tint = AppColors.PrimaryGreen,
                                )
                                Text("Has notes", style = AppTypography.BodySmall, color = AppColors.PrimaryGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButtons(
    currentStatus: FieldNoteStatus,
    isLoading: Boolean,
    onStatusSelected: (FieldNoteStatus) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = AppColors.SurfaceContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.M),
        ) {
            Text(
                text = "Quick Actions",
                style = AppTypography.HeadlineSmall,
                color = AppColors.OnSurface,
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                modifier = Modifier.fillMaxWidth(),
            ) {
                AssistChip(
                    onClick = {
                        val newStatus =
                            if (currentStatus == FieldNoteStatus.DEAD) {
                                FieldNoteStatus.NORMAL
                            } else {
                                FieldNoteStatus.DEAD
                            }
                        onStatusSelected(newStatus)
                    },
                    label = {
                        Text(if (currentStatus == FieldNoteStatus.DEAD) "Mark Alive" else "Mark Dead")
                    },
                    leadingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(2.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    },
                    colors =
                        AssistChipDefaults.assistChipColors(
                            containerColor =
                                if (currentStatus == FieldNoteStatus.DEAD) {
                                    AppColors.Error.copy(alpha = 0.1f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                            labelColor =
                                if (currentStatus == FieldNoteStatus.DEAD) {
                                    AppColors.Error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        ),
                    enabled = !isLoading,
                )

                AssistChip(
                    onClick = {
                        val newStatus =
                            if (currentStatus == FieldNoteStatus.BLOCKED) {
                                FieldNoteStatus.NORMAL
                            } else {
                                FieldNoteStatus.BLOCKED
                            }
                        onStatusSelected(newStatus)
                    },
                    label = {
                        Text(if (currentStatus == FieldNoteStatus.BLOCKED) "Unblock" else "Block Position")
                    },
                    leadingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(2.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Default.Block, contentDescription = null)
                        }
                    },
                    colors =
                        AssistChipDefaults.assistChipColors(
                            containerColor =
                                if (currentStatus == FieldNoteStatus.BLOCKED) {
                                    AppColors.Warning.copy(alpha = 0.1f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                            labelColor =
                                if (currentStatus == FieldNoteStatus.BLOCKED) {
                                    AppColors.Warning
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        ),
                    enabled = !isLoading,
                )
            }
        }
    }
}

@Composable
fun FieldNotesSection(
    fieldNote: FieldNote?,
    isNoteSaving: Boolean,
    onUpdateFieldNote: (String) -> Unit,
    focusManager: FocusManager? = null,
) {
    var noteText by remember(fieldNote?.notes) { mutableStateOf(fieldNote?.notes ?: "") }
    var lastSavedText by remember(fieldNote?.notes) { mutableStateOf(fieldNote?.notes ?: "") }

    // Auto-save functionality with 3-second debounce
    LaunchedEffect(noteText) {
        if (noteText != lastSavedText) {
            delay(3000) // 3 second debounce
            if (noteText != lastSavedText) {
                onUpdateFieldNote(noteText)
                lastSavedText = noteText
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = AppColors.SurfaceContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.M),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Field Notes",
                    style = AppTypography.HeadlineSmall,
                    color = AppColors.OnSurface,
                )

                if (isNoteSaving) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.XS),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(2.dp),
                            strokeWidth = 2.dp,
                        )
                        Text(
                            text = "Saving...",
                            style = AppTypography.BodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.S))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Add your notes here...") },
                placeholder = { Text("Observations, conditions, or other notes about this position") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                enabled = !isNoteSaving,
            )

            Spacer(modifier = Modifier.height(AppSpacing.XS))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "${noteText.length}/500 characters",
                    style = AppTypography.BodySmall,
                    color = if (noteText.length > 500) AppColors.Error else MaterialTheme.colorScheme.onSurfaceVariant,
                )

                fieldNote?.let { note ->
                    Text(
                        text = "Last modified: ${formatTimestamp(note.lastModified)}",
                        style = AppTypography.BodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    // Simple format - just show relative time for now
    val now = currentTimeMillis()
    val diffMinutes = (now - timestamp) / (1000L * 60L)

    return when {
        diffMinutes < 1 -> "Just now"
        diffMinutes < 60 -> "${diffMinutes}m ago"
        diffMinutes < 1440 -> "${diffMinutes / 60L}h ago"
        else -> "${diffMinutes / 1440L}d ago"
    }
}
