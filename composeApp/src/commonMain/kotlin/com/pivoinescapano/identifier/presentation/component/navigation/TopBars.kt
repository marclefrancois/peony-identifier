package com.pivoinescapano.identifier.presentation.component.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTopBar(
    selectedChamp: String? = null,
    selectedParcelle: String? = null,
    onNavigateBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            if (selectedChamp != null && selectedParcelle != null) {
                Column {
                    Text(
                        text = "Position Selection",
                        style = AppTypography.HeadlineSmall,
                        color = AppColors.OnSurface,
                    )
                    Text(
                        text = "Field $selectedChamp, Parcel $selectedParcelle",
                        style = AppTypography.BodyMedium,
                        color = AppColors.OnSurfaceVariant,
                    )
                }
            } else {
                Text(
                    text = "Peony Finder",
                    style = AppTypography.HeadlineSmall,
                    color = AppColors.OnSurface,
                )
            }
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to field selection",
                        tint = AppColors.OnSurface,
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = AppColors.SurfaceContainer,
                titleContentColor = AppColors.OnSurface,
                navigationIconContentColor = AppColors.OnSurface,
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(
    fieldEntry: FieldEntry?,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Position ${fieldEntry?.trou ?: ""}",
                    style = AppTypography.HeadlineSmall,
                    color = AppColors.OnSurface,
                )
                fieldEntry?.let { entry ->
                    Text(
                        text = "Field ${entry.champ} • Parcel ${entry.parcelle} • Row ${entry.rang}",
                        style = AppTypography.BodySmall,
                        color = AppColors.OnSurfaceVariant,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Text(
                    text = "←",
                    style = AppTypography.HeadlineMedium,
                    color = AppColors.OnSurface,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = AppColors.SurfaceContainer,
                titleContentColor = AppColors.OnSurface,
                navigationIconContentColor = AppColors.OnSurface,
            ),
    )
}
