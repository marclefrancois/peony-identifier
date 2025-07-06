package com.pivoinescapano.identifier.presentation.component.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.presentation.component.FullscreenImageViewer
import com.pivoinescapano.identifier.presentation.component.PeonyAsyncImage
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.UniformCard

@Composable
fun FieldEntryCard(entry: FieldEntry) {
    UniformCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "In our plan: ${entry.variete ?: "Unknown"}",
            style = AppTypography.HeadlineSmall,
            color = AppColors.OnSurface,
        )

        // Only show additional info if available
        val additionalInfo =
            buildList {
                entry.annee_plantation?.let { add("Planted: $it") }
                entry.taille?.let { add("Size: $it") }
                entry.etiquette?.let { add("Etiquette: $it") }
                entry.vente?.let { add("Vente: $it") }
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
fun PeonyCard(
    peony: PeonyInfo,
    isExactMatch: Boolean = false,
    isConfirmed: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    var showFullscreenImage by remember { mutableStateOf(false) }
    UniformCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .let { if (onClick != null) it.clickable { onClick() } else it },
        backgroundColor = AppColors.SurfaceContainer,
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
            when {
                isExactMatch -> {
                    Badge(
                        containerColor = AppColors.ExactMatch,
                        contentColor = AppColors.OnPrimary,
                    ) {
                        Text(
                            text = "Exact Match",
                            style = AppTypography.LabelMedium,
                        )
                    }
                }
                isConfirmed -> {
                    Badge(
                        containerColor = AppColors.Info,
                        contentColor = AppColors.OnPrimary,
                    ) {
                        Text(
                            text = "Confirmed",
                            style = AppTypography.LabelMedium,
                        )
                    }
                }
            }
        }

        // Image and info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.M),
        ) {
            // Peony image
            PeonyAsyncImage(
                imageUrl = peony.image,
                contentDescription = "Image of ${peony.cultivar}",
                modifier =
                    Modifier
                        .size(140.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (peony.image != null) {
                                        showFullscreenImage = true
                                    }
                                },
                            )
                        },
            )

            // Peony details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
            ) {
                Text(
                    text = "Originator: ${peony.originator}",
                    style = AppTypography.BodyLarge,
                    color = AppColors.OnSurfaceVariant,
                )
                Text(
                    text = "Date: ${peony.date}",
                    style = AppTypography.BodyLarge,
                    color = AppColors.OnSurfaceVariant,
                )
                Text(
                    text = "Group: ${peony.group}",
                    style = AppTypography.BodyLarge,
                    color = AppColors.OnSurfaceVariant,
                )
            }
        }

        if (peony.description.isNotBlank()) {
            Text(
                text = "Description:",
                style = AppTypography.HeadlineSmall,
                color = AppColors.OnSurface,
            )
            Text(
                text = peony.description.replace(Regex("<[^>]*>"), ""),
                style = AppTypography.BodyLarge,
                color = AppColors.OnSurfaceVariant,
                lineHeight = AppTypography.BodyLarge.lineHeight.times(1.2f),
            )
        }

        // Fullscreen image viewer
        if (showFullscreenImage && peony.image != null) {
            FullscreenImageViewer(
                imageUrl = peony.image,
                contentDescription = "Fullscreen image of ${peony.cultivar}",
                onDismiss = { showFullscreenImage = false },
            )
        }
    }
}
