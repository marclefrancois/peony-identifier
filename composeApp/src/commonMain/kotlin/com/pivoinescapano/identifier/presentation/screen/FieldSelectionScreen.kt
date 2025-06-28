package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.presentation.state.FieldSelectionState
import com.pivoinescapano.identifier.presentation.theme.*
import com.pivoinescapano.identifier.presentation.viewmodel.FieldSelectionViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldSelectionScreen(
    initialChamp: String? = null,
    initialParcelle: String? = null,
    onContinue: (champ: String, parcelle: String) -> Unit,
    viewModel: FieldSelectionViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Restore previous selections when coming back from position screen
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.EdgePadding)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.SectionSpacing)
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
        ) {
            Text(
                text = "Select Field Location",
                style = AppTypography.HeadlineLarge,
                color = AppColors.OnSurface,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Choose the field and parcel to identify peonies",
                style = AppTypography.BodyLarge,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        if (uiState.isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.L)
                ) {
                    CircularProgressIndicator(color = AppColors.PrimaryGreen)
                    Text(
                        text = "Loading field data...",
                        style = AppTypography.BodyMedium,
                        color = AppColors.OnSurfaceVariant
                    )
                }
            }
        } else {
            // Selection content
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.L)
            ) {
                // Field (Champ) Selection
                item {
                    FieldSelectionCard(
                        title = "Field (Champ)",
                        subtitle = if (uiState.selectedChamp != null) "Selected: ${uiState.selectedChamp}" else "Select a field",
                        selected = uiState.selectedChamp != null,
                        onClick = { /* We'll show dropdown instead */ }
                    ) {
                        LargeDropdownSelector(
                            label = "Choose Field",
                            options = uiState.availableChamps,
                            selectedOption = uiState.selectedChamp,
                            onSelectionChanged = viewModel::onChampSelected
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
                            onClick = { /* We'll show dropdown instead */ }
                        ) {
                            LargeDropdownSelector(
                                label = "Choose Parcel",
                                options = uiState.availableParcelles,
                                selectedOption = uiState.selectedParcelle,
                                onSelectionChanged = viewModel::onParcelleSelected
                            )
                        }
                    }
                }
                
                // Preview Card - only show if both are selected
                if (uiState.selectedChamp != null && uiState.selectedParcelle != null) {
                    item {
                        UniformCard(
                            backgroundColor = AppColors.PrimaryContainer,
                            contentColor = AppColors.OnPrimaryContainer
                        ) {
                            Text(
                                text = "Selection Preview",
                                style = AppTypography.HeadlineSmall,
                                color = AppColors.OnPrimaryContainer
                            )
                            
                            Text(
                                text = "Field: ${uiState.selectedChamp}",
                                style = AppTypography.BodyLarge,
                                color = AppColors.OnPrimaryContainer
                            )
                            
                            Text(
                                text = "Parcel: ${uiState.selectedParcelle}",
                                style = AppTypography.BodyLarge,
                                color = AppColors.OnPrimaryContainer
                            )
                        }
                    }
                }
            }
            
            // Continue Button
            ContinueButton(
                onClick = {
                    val champ = uiState.selectedChamp
                    val parcelle = uiState.selectedParcelle
                    if (champ != null && parcelle != null) {
                        onContinue(champ, parcelle)
                    }
                },
                enabled = uiState.canContinue,
                text = "Continue to Position Selection"
            )
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
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = { },
            readOnly = true,
            label = { 
                Text(
                    text = label, 
                    style = AppTypography.FieldSelectionDropdown
                ) 
            },
            placeholder = { 
                Text(
                    text = "Select $label", 
                    style = AppTypography.FieldSelectionDropdown
                ) 
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .height(AppSpacing.FieldSelectionDropdownHeight)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            textStyle = AppTypography.FieldSelectionDropdown,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.PrimaryGreen,
                focusedLabelColor = AppColors.PrimaryGreen
            )
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = option,
                            style = AppTypography.FieldSelectionDropdown
                        ) 
                    },
                    onClick = {
                        onSelectionChanged(option)
                        expanded = false
                    }
                )
            }
        }
    }
}