package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.platform.BackHandler
import com.pivoinescapano.identifier.presentation.component.content.ErrorContent
import com.pivoinescapano.identifier.presentation.component.content.PeonyDetailsContent
import com.pivoinescapano.identifier.presentation.component.navigation.DetailsTopBar
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyDetailViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeonyDetailScreen(
    champ: String,
    parcelle: String,
    rang: String,
    trou: String,
    onNavigateBack: () -> Unit,
    viewModel: PeonyDetailViewModel = koinInject { parametersOf(champ, parcelle, rang, trou) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle Android physical back button
    BackHandler(enabled = true) {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            DetailsTopBar(
                fieldEntry = uiState.fieldEntry,
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
                        peony = uiState.peony,
                        fuzzyMatches = uiState.fuzzyMatches,
                        fieldEntry = uiState.fieldEntry,
                        fieldNote = uiState.fieldNote,
                        isNoteSaving = uiState.isNoteSaving,
                        isPeonyConfirmed = uiState.isPeonyConfirmed,
                        onFuzzyMatchSelected = viewModel::onFuzzyMatchSelected,
                        onUpdateFieldNote = viewModel::updateFieldNote,
                        onUpdateFieldNoteStatus = viewModel::updateFieldNoteStatus,
                    )
                }
            }
        }
    }
}
