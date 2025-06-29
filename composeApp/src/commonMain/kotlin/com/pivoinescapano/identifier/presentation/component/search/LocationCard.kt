package com.pivoinescapano.identifier.presentation.component.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.domain.model.PeonyLocation
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.UniformCard

@Composable
fun LocationCard(
    location: PeonyLocation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    UniformCard(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() },
        backgroundColor = AppColors.SurfaceContainer,
        elevation = 2.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.CardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Location info section
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.M),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Location icon
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = AppColors.PrimaryGreen,
                    modifier = Modifier.size(20.dp),
                )

                // Location details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.XS),
                ) {
                    // Variety name
                    Text(
                        text = location.variete ?: "Unknown variety",
                        style = AppTypography.BodyLarge,
                        color = AppColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    // Location details
                    Text(
                        text = location.fullLocationName,
                        style = AppTypography.BodyMedium,
                        color = AppColors.OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    // Size information if available
                    location.taille?.let { size ->
                        Text(
                            text = "Size: $size",
                            style = AppTypography.BodySmall,
                            color = AppColors.OnSurfaceVariant,
                        )
                    }
                }
            }

            // Navigation arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to location",
                tint = AppColors.PrimaryGreen,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun NoResultsCard(
    searchQuery: String,
    modifier: Modifier = Modifier,
) {
    UniformCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = AppColors.SurfaceContainer,
        elevation = 1.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.L),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
        ) {
            Text(
                text = "No results found",
                style = AppTypography.HeadlineSmall,
                color = AppColors.OnSurface,
            )

            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "No peony varieties found matching \"$searchQuery\"",
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant,
                )
            }

            Text(
                text = "Try searching with a different variety name or check for typos",
                style = AppTypography.BodySmall,
                color = AppColors.OnSurfaceVariant,
            )
        }
    }
}
