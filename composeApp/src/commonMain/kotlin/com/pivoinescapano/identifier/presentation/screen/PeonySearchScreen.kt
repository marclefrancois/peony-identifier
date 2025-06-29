package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.platform.BackHandler
import com.pivoinescapano.identifier.presentation.component.content.ErrorContent
import com.pivoinescapano.identifier.presentation.component.search.LocationCard
import com.pivoinescapano.identifier.presentation.component.search.NoResultsCard
import com.pivoinescapano.identifier.presentation.component.search.PeonySearchBar
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.viewmodel.PeonySearchViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeonySearchScreen(
    restoredSearchTerm: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (champ: String, parcelle: String, rang: String, trou: String, searchTerm: String?) -> Unit,
    viewModel: PeonySearchViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle Android physical back button
    BackHandler(enabled = true) {
        onNavigateBack()
    }

    // Restore search term when returning from detail
    LaunchedEffect(restoredSearchTerm) {
        if (restoredSearchTerm != null) {
            viewModel.restoreSearchTerm(restoredSearchTerm)
        }
    }

    // Clear error when screen loads
    LaunchedEffect(Unit) {
        if (uiState.errorMessage != null) {
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search Peonies",
                        style = AppTypography.HeadlineMedium,
                        color = AppColors.OnSurface,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.OnSurface,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.BarColor,
                        titleContentColor = AppColors.OnSurface,
                        navigationIconContentColor = AppColors.OnSurface,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = AppSpacing.EdgePadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
        ) {
            // Search bar
            PeonySearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                suggestions = uiState.suggestions,
                onSuggestionClick = viewModel::onSuggestionSelected,
                modifier = Modifier.padding(top = AppSpacing.M),
            )

            // Content area
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.errorMessage != null -> {
                    ErrorContent(
                        error = uiState.errorMessage!!,
                        onDismiss = viewModel::clearError,
                    )
                }

                uiState.hasSearched && uiState.searchResults.isEmpty() -> {
                    NoResultsCard(
                        searchQuery = uiState.searchQuery,
                        modifier = Modifier.padding(top = AppSpacing.L),
                    )
                }

                uiState.searchResults.isNotEmpty() -> {
                    SearchResultsContent(
                        searchResults = uiState.searchResults,
                        onLocationClick = { location ->
                            onNavigateToDetail(
                                location.champ,
                                location.parcelle,
                                location.rang,
                                location.trou,
                                uiState.searchQuery.takeIf { it.isNotBlank() },
                            )
                        },
                    )
                }

                else -> {
                    EmptyStateContent()
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
        ) {
            CircularProgressIndicator(
                color = AppColors.PrimaryGreen,
            )
            Text(
                text = "Searching peonies...",
                style = AppTypography.BodyMedium,
                color = AppColors.OnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    searchResults: List<com.pivoinescapano.identifier.domain.model.PeonyLocation>,
    onLocationClick: (com.pivoinescapano.identifier.domain.model.PeonyLocation) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = AppSpacing.S),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text(
                text = "${searchResults.size} location${if (searchResults.size != 1) "s" else ""} found",
                style = AppTypography.BodyMedium,
                color = AppColors.OnSurfaceVariant,
                modifier = Modifier.padding(bottom = AppSpacing.S),
            )
        }

        items(searchResults) { location ->
            LocationCard(
                location = location,
                onClick = { onLocationClick(location) },
            )
        }
    }
}

@Composable
private fun EmptyStateContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
            modifier = Modifier.padding(AppSpacing.L),
        ) {
            Text(
                text = "🔍",
                style = AppTypography.HeadlineLarge,
            )
            Text(
                text = "Search for Peony Varieties",
                style = AppTypography.HeadlineSmall,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Type a peony variety name to find all field locations where it can be found",
                style = AppTypography.BodyMedium,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
