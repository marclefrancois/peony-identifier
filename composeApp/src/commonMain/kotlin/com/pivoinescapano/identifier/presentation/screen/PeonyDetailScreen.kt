package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.platform.BackHandler
import com.pivoinescapano.identifier.presentation.component.content.ErrorContent
import com.pivoinescapano.identifier.presentation.component.content.PeonyDetailsContent
import com.pivoinescapano.identifier.presentation.component.navigation.DetailsTopBar
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyIdentifierViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeonyDetailScreen(
    champ: String,
    parcelle: String,
    rang: String,
    trou: String,
    onNavigateBack: () -> Unit,
    viewModel: PeonyIdentifierViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Initialize with navigation parameters
    LaunchedEffect(champ, parcelle, rang, trou) {
        if (uiState.selectedChamp != champ) {
            viewModel.onChampSelected(champ)
        }
        if (uiState.selectedParcelle != parcelle) {
            viewModel.onParcelleSelected(parcelle)
        }
        if (uiState.selectedRang != rang) {
            viewModel.onRangSelected(rang)
        }
        if (uiState.selectedTrou != trou) {
            viewModel.onTrouSelected(trou)
        }
    }

    // Handle Android physical back button
    BackHandler(enabled = true) {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            DetailsTopBar(
                fieldEntry = uiState.currentFieldEntry,
                onBackClick = onNavigateBack,
            )
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        ErrorContent(
                            error = uiState.error ?: "Unknown error occurred",
                            onDismiss = viewModel::clearError,
                        )
                    }
                }

                else -> {
                    PeonyDetailsContent(
                        peony = uiState.currentPeony,
                        fuzzyMatches = uiState.fuzzyMatches,
                        fieldEntry = uiState.currentFieldEntry,
                        onFuzzyMatchSelected = viewModel::onFuzzyMatchSelected,
                    )
                }
            }
        }
    }
}