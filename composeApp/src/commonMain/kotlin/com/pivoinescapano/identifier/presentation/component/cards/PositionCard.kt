package com.pivoinescapano.identifier.presentation.component.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.UniformCard

@Composable
fun PositionCard(
    position: String,
    entry: FieldEntry?,
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
