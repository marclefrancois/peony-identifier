package com.pivoinescapano.identifier.presentation.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.pivoinescapano.identifier.domain.model.ExportFilter
import com.pivoinescapano.identifier.domain.model.ExportFormat
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExportDialog(
    availableFields: List<String>,
    onExport: (ExportFilter) -> Unit,
    onShare: (ExportFilter) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedFields by remember { mutableStateOf(emptySet<String>()) }
    var selectedStatuses by remember { mutableStateOf(emptySet<FieldNoteStatus>()) }
    var includeEmptyNotes by remember { mutableStateOf(true) }
    var exportFormat by remember { mutableStateOf(ExportFormat.CSV) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Export Field Notes",
                style = AppTypography.HeadlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
            ) {
                // Export Format Selection
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
                            text = "Export Format",
                            style = AppTypography.HeadlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.S))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = exportFormat == ExportFormat.CSV,
                                onClick = { exportFormat = ExportFormat.CSV },
                            )
                            Text("CSV (Spreadsheet)")

                            Spacer(modifier = Modifier.width(AppSpacing.L))

                            RadioButton(
                                selected = exportFormat == ExportFormat.JSON,
                                onClick = { exportFormat = ExportFormat.JSON },
                            )
                            Text("JSON (Backup)")
                        }
                    }
                }

                // Field Filter
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
                            text = "Fields to Export",
                            style = AppTypography.HeadlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Text(
                            text = "Leave empty to export all fields",
                            style = AppTypography.BodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.S))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.XS),
                        ) {
                            availableFields.forEach { field ->
                                FilterChip(
                                    selected = selectedFields.contains(field),
                                    onClick = {
                                        selectedFields =
                                            if (selectedFields.contains(field)) {
                                                selectedFields - field
                                            } else {
                                                selectedFields + field
                                            }
                                    },
                                    label = { Text("Field $field") },
                                )
                            }
                        }
                    }
                }

                // Status Filter
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
                            text = "Status Filter",
                            style = AppTypography.HeadlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Text(
                            text = "Leave empty to include all statuses",
                            style = AppTypography.BodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.S))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.XS),
                        ) {
                            FieldNoteStatus.entries.forEach { status ->
                                FilterChip(
                                    selected = selectedStatuses.contains(status),
                                    onClick = {
                                        selectedStatuses =
                                            if (selectedStatuses.contains(status)) {
                                                selectedStatuses - status
                                            } else {
                                                selectedStatuses + status
                                            }
                                    },
                                    label = { Text(status.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                )
                            }
                        }
                    }
                }

                // Options
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
                            text = "Export Options",
                            style = AppTypography.HeadlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.S))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = includeEmptyNotes,
                                onCheckedChange = { includeEmptyNotes = it },
                            )
                            Text("Include positions with empty notes")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row {
                TextButton(
                    onClick = {
                        val filter =
                            ExportFilter(
                                fields = selectedFields,
                                statuses = selectedStatuses,
                                includeEmptyNotes = includeEmptyNotes,
                                format = exportFormat,
                            )
                        onShare(filter)
                    },
                ) {
                    Text("Share")
                }

                Spacer(modifier = Modifier.width(AppSpacing.S))

                TextButton(
                    onClick = {
                        val filter =
                            ExportFilter(
                                fields = selectedFields,
                                statuses = selectedStatuses,
                                includeEmptyNotes = includeEmptyNotes,
                                format = exportFormat,
                            )
                        onExport(filter)
                    },
                ) {
                    Text("Export")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
