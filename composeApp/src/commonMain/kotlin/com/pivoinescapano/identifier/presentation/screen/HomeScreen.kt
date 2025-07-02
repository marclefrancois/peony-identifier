package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.HomeTileCard
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToIdentify: () -> Unit,
    onNavigateToFieldNotes: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        horizontal = AppSpacing.L,
                        vertical = AppSpacing.XL,
                    )
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.L),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // App Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
            ) {
                Text(
                    text = "Peony Identifier",
                    style = AppTypography.HeadlineLarge,
                    color = AppColors.PrimaryGreen,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Discover and document peonies in the field",
                    style = AppTypography.BodyLarge,
                    color = AppColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            // Navigation Tiles
            Column(
                verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
            ) {
                HomeTileCard(
                    title = "Search Peonies",
                    description = "Find peony varieties across all field locations",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AppColors.PrimaryGreen,
                        )
                    },
                    onClick = onNavigateToSearch,
                )

                HomeTileCard(
                    title = "Identify Peony",
                    description = "Browse by field location to identify varieties",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.LocalFlorist,
                            contentDescription = "Identify",
                            tint = AppColors.PrimaryGreen,
                        )
                    },
                    onClick = onNavigateToIdentify,
                )

                HomeTileCard(
                    title = "Field Notes",
                    description = "View and manage your field observations",
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Note,
                            contentDescription = "Field Notes",
                            tint = AppColors.PrimaryGreen,
                        )
                    },
                    onClick = onNavigateToFieldNotes,
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onNavigateToSearch = {},
            onNavigateToIdentify = {},
            onNavigateToFieldNotes = {},
        )
    }
}
