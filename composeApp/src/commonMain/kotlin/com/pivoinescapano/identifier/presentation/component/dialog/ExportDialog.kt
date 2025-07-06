package com.pivoinescapano.identifier.presentation.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.pivoinescapano.identifier.domain.model.ExportFilter
import com.pivoinescapano.identifier.domain.model.ExportFormat
import com.pivoinescapano.identifier.presentation.theme.AppTypography

@Composable
fun ExportDialog(
    onShare: (ExportFilter) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Share Field Notes",
                style = AppTypography.HeadlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = "Share all field notes as a CSV file?",
                style = AppTypography.BodyLarge,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val filter =
                        ExportFilter(
                            fields = emptySet(),
                            statuses = emptySet(),
                            includeEmptyNotes = true,
                            format = ExportFormat.CSV,
                        )
                    onShare(filter)
                },
            ) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
