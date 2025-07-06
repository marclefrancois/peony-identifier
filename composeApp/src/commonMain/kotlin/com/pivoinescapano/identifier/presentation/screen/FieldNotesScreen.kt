package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.platform.currentTimeMillis
import com.pivoinescapano.identifier.presentation.state.SortOrder
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.viewmodel.FieldNotesViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldNotesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String, String, String, String) -> Unit,
    viewModel: FieldNotesViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Field Notes",
                        style = AppTypography.HeadlineMedium,
                        color = AppColors.OnSurface,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.OnSurface,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.showExportDialog()
                    }) {
                        Icon(
                            Icons.Default.GetApp,
                            contentDescription = "Export",
                            tint = AppColors.OnSurface,
                        )
                    }

                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort",
                                tint = AppColors.OnSurface,
                            )
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Position") },
                                onClick = {
                                    viewModel.updateSortOrder(SortOrder.POSITION)
                                    showSortMenu = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Date Created") },
                                onClick = {
                                    viewModel.updateSortOrder(SortOrder.DATE_CREATED)
                                    showSortMenu = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Date Modified") },
                                onClick = {
                                    viewModel.updateSortOrder(SortOrder.DATE_MODIFIED)
                                    showSortMenu = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Status") },
                                onClick = {
                                    viewModel.updateSortOrder(SortOrder.STATUS)
                                    showSortMenu = false
                                },
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.showClearConfirmDialog() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Clear All",
                            tint = AppColors.OnSurface,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.BarColor,
                        titleContentColor = AppColors.OnSurface,
                        navigationIconContentColor = AppColors.OnSurface,
                        actionIconContentColor = AppColors.OnSurface,
                    ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            SearchAndFilters(
                searchQuery = state.searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                selectedStatus = state.selectedStatus,
                onStatusFilterChange = viewModel::updateStatusFilter,
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredNotes.isEmpty()) {
                EmptyNotesMessage()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
                ) {
                    items(state.filteredNotes) { note ->
                        FieldNoteCard(
                            note = note,
                            onNoteClick = {
                                onNavigateToDetail(note.champ, note.parcelle, note.rang, note.trou)
                            },
                            onDeleteNote = { viewModel.deleteNote(note.id) },
                        )
                    }
                }
            }
        }
    }

    if (state.isClearConfirmDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.hideClearConfirmDialog() },
            title = { Text("Clear All Notes?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllNotesWithoutExport()
                    },
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideClearConfirmDialog() },
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun SearchAndFilters(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatus: FieldNoteStatus?,
    onStatusFilterChange: (FieldNoteStatus?) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(AppSpacing.L),
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search notes, varieties, or positions") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(AppSpacing.M))

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
        ) {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusFilterChange(null) },
                label = { Text("All") },
            )
            FilterChip(
                selected = selectedStatus == FieldNoteStatus.NORMAL,
                onClick = { onStatusFilterChange(FieldNoteStatus.NORMAL) },
                label = { Text("Normal") },
            )
            FilterChip(
                selected = selectedStatus == FieldNoteStatus.DEAD,
                onClick = { onStatusFilterChange(FieldNoteStatus.DEAD) },
                label = { Text("Dead") },
            )
            FilterChip(
                selected = selectedStatus == FieldNoteStatus.BLOCKED,
                onClick = { onStatusFilterChange(FieldNoteStatus.BLOCKED) },
                label = { Text("Blocked") },
            )
        }
    }
}

@Composable
private fun FieldNoteCard(
    note: FieldNote,
    onNoteClick: () -> Unit,
    onDeleteNote: () -> Unit,
) {
    Card(
        onClick = onNoteClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.L),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.L),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Field ${note.champ} • ${note.parcelle} • Row ${note.rang} • Position ${note.trou}",
                        style = AppTypography.HeadlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.PrimaryGreen,
                    )

                    note.variety?.let { variety ->
                        Text(
                            text = variety,
                            style = AppTypography.BodyMedium,
                            color = AppColors.OnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }

                StatusIndicator(
                    status = note.status,
                    hasNotes = note.notes.isNotEmpty(),
                    modifier = Modifier.padding(start = AppSpacing.S),
                )
            }

            if (note.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppSpacing.M))
                Text(
                    text = note.notes,
                    style = AppTypography.BodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.M))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatTimestamp(note.lastModified),
                    style = AppTypography.BodySmall,
                    color = AppColors.OnSurfaceVariant,
                )

                IconButton(
                    onClick = onDeleteNote,
                    modifier = Modifier.padding(0.dp),
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete note",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyNotesMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "No field notes found",
                style = AppTypography.HeadlineSmall,
                color = AppColors.OnSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(AppSpacing.S))
            Text(
                text = "Add notes to peonies from the detail screen",
                style = AppTypography.BodyMedium,
                color = AppColors.OnSurfaceVariant,
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val currentTime = currentTimeMillis()
    val diffInMillis = currentTime - timestamp
    val diffInMinutes = diffInMillis / (1000L * 60L)
    val diffInHours = diffInMinutes / 60L
    val diffInDays = diffInHours / 24L

    return when {
        diffInMinutes < 1 -> "Just now"
        diffInMinutes < 60 -> "${diffInMinutes}m ago"
        diffInHours < 24 -> "${diffInHours}h ago"
        diffInDays < 7 -> "${diffInDays}d ago"
        else -> {
            val weeks = diffInDays / 7L
            "${weeks}w ago"
        }
    }
}

@Composable
private fun StatusIndicator(
    status: FieldNoteStatus,
    hasNotes: Boolean,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, iconTint) =
        when (status) {
            FieldNoteStatus.DEAD -> Color(0xFFF44336) to Color.White
            FieldNoteStatus.BLOCKED -> Color(0xFFFF9800) to Color.White
            FieldNoteStatus.NORMAL -> {
                if (hasNotes) {
                    Color(0xFF2196F3) to Color.White
                } else {
                    MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                }
            }
        }

    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val icon =
                when (status) {
                    FieldNoteStatus.DEAD -> Icons.Default.Delete
                    FieldNoteStatus.BLOCKED -> Icons.Default.Block
                    FieldNoteStatus.NORMAL -> if (hasNotes) Icons.AutoMirrored.Filled.Notes else null
                }

            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(2.dp),
                )
            }

            Text(
                text =
                    when (status) {
                        FieldNoteStatus.DEAD -> "Dead"
                        FieldNoteStatus.BLOCKED -> "Blocked"
                        FieldNoteStatus.NORMAL -> if (hasNotes) "Notes" else "Normal"
                    },
                style = AppTypography.LabelSmall,
                color = iconTint,
            )
        }
    }
}
