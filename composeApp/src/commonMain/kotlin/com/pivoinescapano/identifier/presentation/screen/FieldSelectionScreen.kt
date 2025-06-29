package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.repository.FieldRepository
import com.pivoinescapano.identifier.data.usecase.GetFieldEntriesUseCase
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.ContinueButton
import com.pivoinescapano.identifier.presentation.theme.FieldSelectionCard
import com.pivoinescapano.identifier.presentation.theme.UniformCard
import com.pivoinescapano.identifier.presentation.viewmodel.FieldSelectionViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldSelectionScreen(
    initialChamp: String? = null,
    initialParcelle: String? = null,
    onContinue: (champ: String, parcelle: String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: FieldSelectionViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Restore previous selections when coming back from navigation
    LaunchedEffect(initialChamp, initialParcelle) {
        if (initialChamp != null && initialParcelle != null) {
            viewModel.restoreSelections(initialChamp, initialParcelle)
        }
    }

    // Show error snackbar
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // In a real app, you'd show a snackbar here
            viewModel.clearError()
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal =  AppSpacing.EdgePadding)
            ) {
                // Header
                Column(
                    modifier = Modifier.padding(bottom = AppSpacing.M),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
                ) {
                    Text(
                        text = "Select Field Location",
                        style = AppTypography.HeadlineLarge,
                        color = AppColors.OnSurface,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "Choose the field and parcel to identify peonies",
                        style = AppTypography.BodyLarge,
                        color = AppColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }

                // Content area that takes available space
                if (uiState.isLoading) {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.L),
                        ) {
                            CircularProgressIndicator(color = AppColors.PrimaryGreen)
                            Text(
                                text = "Loading field data...",
                                style = AppTypography.BodyMedium,
                                color = AppColors.OnSurfaceVariant,
                            )
                        }
                    }
                } else {
                    // Selection content
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.L),
                    ) {
                        // Field (Champ) Selection
                        item {
                            FieldSelectionCard(
                                title = "Field (Champ)",
                                subtitle = if (uiState.selectedChamp != null) "Selected: ${uiState.selectedChamp}" else "Select a field",
                                selected = uiState.selectedChamp != null,
                                onClick = { /* We'll show dropdown instead */ },
                            ) {
                                LargeDropdownSelector(
                                    label = "Choose Field",
                                    options = uiState.availableChamps,
                                    selectedOption = uiState.selectedChamp,
                                    onSelectionChanged = viewModel::onChampSelected,
                                )
                            }
                        }

                        // Parcel (Parcelle) Selection - only show if field is selected
                        if (uiState.selectedChamp != null) {
                            item {
                                FieldSelectionCard(
                                    title = "Parcel (Parcelle)",
                                    subtitle = if (uiState.selectedParcelle != null) "Selected: ${uiState.selectedParcelle}" else "Select a parcel",
                                    selected = uiState.selectedParcelle != null,
                                    onClick = { /* We'll show dropdown instead */ },
                                ) {
                                    LargeDropdownSelector(
                                        label = "Choose Parcel",
                                        options = uiState.availableParcelles,
                                        selectedOption = uiState.selectedParcelle,
                                        onSelectionChanged = viewModel::onParcelleSelected,
                                    )
                                }
                            }
                        }

                        // Preview Card - only show if both are selected
                        if (uiState.selectedChamp != null && uiState.selectedParcelle != null) {
                            item {
                                UniformCard(
                                    backgroundColor = AppColors.PrimaryContainer,
                                    contentColor = AppColors.OnPrimaryContainer,
                                ) {
                                    Text(
                                        text = "Selection Preview",
                                        style = AppTypography.HeadlineSmall,
                                        color = AppColors.OnPrimaryContainer,
                                    )

                                    Text(
                                        text = "Field: ${uiState.selectedChamp}",
                                        style = AppTypography.BodyLarge,
                                        color = AppColors.OnPrimaryContainer,
                                    )

                                    Text(
                                        text = "Parcel: ${uiState.selectedParcelle}",
                                        style = AppTypography.BodyLarge,
                                        color = AppColors.OnPrimaryContainer,
                                    )
                                }
                            }
                        }
                    }
                }

                // Continue Button at bottom
                ContinueButton(
                    onClick = {
                        val champ = uiState.selectedChamp
                        val parcelle = uiState.selectedParcelle
                        if (champ != null && parcelle != null) {
                            onContinue(champ, parcelle)
                        }
                    },
                    enabled = uiState.canContinue,
                    text = "Continue to Position Selection",
                    modifier = Modifier.padding(top = AppSpacing.SectionSpacing, bottom = AppSpacing.M),
                )
            }

            // Floating Search Button positioned over the content
            FloatingActionButton(
                shape = CircleShape,
                onClick = onNavigateToSearch,
                containerColor = AppColors.Error,
                contentColor = AppColors.OnPrimary,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = AppSpacing.ContinueButtonHeight + AppSpacing.M)
                        .padding(AppSpacing.M)
                        .size(64.dp),
                elevation =
                    FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp,
                        hoveredElevation = 10.dp,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search peonies",
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LargeDropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onSelectionChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = { },
            readOnly = true,
            label = {
                Text(
                    text = label,
                    style = AppTypography.FieldSelectionDropdown,
                )
            },
            placeholder = {
                Text(
                    text = "Select $label",
                    style = AppTypography.FieldSelectionDropdown,
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(AppSpacing.FieldSelectionDropdownHeight)
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            textStyle = AppTypography.FieldSelectionDropdown,
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.PrimaryGreen,
                    focusedLabelColor = AppColors.PrimaryGreen,
                ),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = AppTypography.FieldSelectionDropdown,
                        )
                    },
                    onClick = {
                        onSelectionChanged(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun previewFieldSelectionScreen() {
    FieldSelectionScreen(
        initialChamp = null,
        initialParcelle = null,
        onContinue = { _, _ -> },
        onNavigateToSearch = {},
        viewModel = FieldSelectionViewModel(
            GetFieldEntriesUseCase(
                object: FieldRepository {
                    override suspend fun getFieldEntries(fieldNumber: String): List<FieldEntry> {
                        return emptyList()
                    }

                    override suspend fun getDistinctChamps(): List<String> {
                        return emptyList()
                    }

                    override suspend fun getDistinctParcelles(champ: String): List<String> {
                        return emptyList()
                    }

                    override suspend fun getDistinctRangs(champ: String, parcelle: String): List<String> {
                        return emptyList()
                    }

                    override suspend fun getDistinctTrous(champ: String, parcelle: String, rang: String): List<String> {
                        return emptyList()
                    }

                    override suspend fun getFieldEntry(champ: String, parcelle: String, rang: String, trou: String): FieldEntry? {
                        return null
                    }

                    override suspend fun getRowEntries(
                        champ: String,
                        parcelle: String,
                        rang: String
                    ): List<FieldEntry> {
                        return emptyList()
                    }

                    override suspend fun getAllFieldEntries(): List<FieldEntry> {
                        return emptyList()
                    }

                }
            )
        )
    )
}
