package com.pivoinescapano.identifier.presentation.component.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.presentation.state.PeonyIdentifierState
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.EnhancedBottomNavigationBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleRowSelectionBar(
    uiState: PeonyIdentifierState,
    onPreviousRow: () -> Unit,
    onNextRow: () -> Unit,
    onReset: () -> Unit,
) {
    EnhancedBottomNavigationBar(
        modifier =
            Modifier
                .combinedClickable(
                    onLongClick = onReset,
                    onClick = {},
                ),
    ) {
        val selectedRang = uiState.selectedRang
        val availableRangs = uiState.availableRangs
        val isEnabled = !uiState.isLoading && uiState.selectedParcelle != null && availableRangs.isNotEmpty()

        if (isEnabled && selectedRang != null) {
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

@Composable
fun SimpleRowControl(
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
                        repeat(if (totalRows < 10) totalRows else 10) { index -> // Limit to 10 dots
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
