package com.pivoinescapano.identifier.presentation.component.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.UniformCard

@Composable
fun PositionCard(
    position: String,
    entry: FieldEntry?,
    fieldNote: FieldNote? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    UniformCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        elevation = if (isSelected) 4.dp else 2.dp,
        backgroundColor = if (isSelected) AppColors.PrimaryContainer else AppColors.SurfaceContainer,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Position $position",
                    style = AppTypography.LabelMedium,
                    color = AppColors.OnSurfaceVariant,
                )
                entry?.variety?.let { variety ->
                    Text(
                        text = if (variety.uppercase() == "VIDE") "Empty" else variety,
                        style = AppTypography.HeadlineSmall,
                        color = if (variety.uppercase() == "VIDE") AppColors.OnSurfaceVariant else AppColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } ?: run {
                    Text(
                        text = "Unknown variety",
                        style = AppTypography.HeadlineSmall,
                        color = AppColors.OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                entry?.size?.let { size ->
                    Text(
                        text = "Size: $size",
                        style = AppTypography.BodySmall,
                        color = AppColors.OnSurfaceVariant,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Field note indicators
                fieldNote?.let { note ->
                    FieldNoteIndicators(fieldNote = note)
                }

                Text(
                    text = "→",
                    style = AppTypography.HeadlineMedium,
                    color = AppColors.OnSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun FieldNoteIndicators(fieldNote: FieldNote) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Show variety confirmation indicator
        if (fieldNote.variety != null) {
            FieldNoteIndicator(backgroundColor = AppColors.PrimaryGreen)
        }

        // Show status indicators
        when (fieldNote.status) {
            FieldNoteStatus.DEAD -> {
                FieldNoteIndicator(backgroundColor = AppColors.Error)
            }
            FieldNoteStatus.BLOCKED -> {
                FieldNoteIndicator(backgroundColor = AppColors.Warning)
            }
            FieldNoteStatus.NORMAL -> {
                // Show notes indicator if there are notes
                if (fieldNote.notes.isNotEmpty()) {
                    FieldNoteIndicator(backgroundColor = AppColors.Info)
                }
            }
        }
    }
}

@Composable
private fun FieldNoteIndicator(backgroundColor: Color) {
    Card(
        modifier = Modifier.size(20.dp),
        shape = CircleShape,
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        // Empty box - just showing colored circle without icon
        Box(
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
