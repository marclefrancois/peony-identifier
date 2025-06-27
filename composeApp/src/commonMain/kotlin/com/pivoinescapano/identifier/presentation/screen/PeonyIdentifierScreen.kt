package com.pivoinescapano.identifier.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.presentation.state.PeonyIdentifierState
import com.pivoinescapano.identifier.presentation.viewmodel.PeonyIdentifierViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeonyIdentifierScreen(
    viewModel: PeonyIdentifierViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Main content area for peony details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error!!,
                        onDismiss = viewModel::clearError,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.showPeonyDetails -> {
                    PeonyDetailsContent(
                        peony = uiState.currentPeony,
                        fuzzyMatches = uiState.fuzzyMatches,
                        fieldEntry = uiState.currentFieldEntry,
                        onFuzzyMatchSelected = viewModel::onFuzzyMatchSelected
                    )
                }
                else -> {
                    Text(
                        text = "Select a field position to view peony details",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bottom selection area with cascading spinners
        SelectionControls(
            uiState = uiState,
            onChampSelected = viewModel::onChampSelected,
            onParcelleSelected = viewModel::onParcelleSelected,
            onRangSelected = viewModel::onRangSelected,
            onTrouSelected = viewModel::onTrouSelected,
            onReset = viewModel::reset
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}

@Composable
private fun PeonyDetailsContent(
    peony: PeonyInfo?,
    fuzzyMatches: List<PeonyInfo>,
    fieldEntry: com.pivoinescapano.identifier.data.model.FieldEntry?,
    onFuzzyMatchSelected: (PeonyInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Field entry info
        fieldEntry?.let { entry ->
            item {
                FieldEntryCard(entry)
            }
        }
        
        // Exact peony match
        peony?.let { p ->
            item {
                PeonyCard(p, isExactMatch = true)
            }
        }
        
        // Fuzzy matches
        if (fuzzyMatches.isNotEmpty()) {
            item {
                Text(
                    text = if (peony == null) "Possible matches:" else "Other similar varieties:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(fuzzyMatches) { match ->
                PeonyCard(
                    peony = match,
                    isExactMatch = false,
                    onClick = { onFuzzyMatchSelected(match) }
                )
            }
        }
    }
}

@Composable
private fun FieldEntryCard(entry: com.pivoinescapano.identifier.data.model.FieldEntry) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Field Position",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Field: ${entry.champ}")
            Text("Parcel: ${entry.parcelle}")
            Text("Row: ${entry.rang}")
            Text("Position: ${entry.trou}")
            Text(
                text = "Variety: ${entry.variete}",
                fontWeight = FontWeight.Medium
            )
            entry.annee_plantation?.let {
                Text("Planted: $it")
            }
            entry.taille?.let {
                Text("Size: $it")
            }
        }
    }
}

@Composable
private fun PeonyCard(
    peony: PeonyInfo,
    isExactMatch: Boolean,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick ?: {},
        colors = if (isExactMatch) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = peony.cultivar,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (isExactMatch) {
                    Badge {
                        Text("Exact Match")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Originator: ${peony.originator}")
            Text("Date: ${peony.date}")
            Text("Group: ${peony.group}")
            
            if (peony.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Description:",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = peony.description.replace(Regex("<[^>]*>"), ""), // Strip HTML tags
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionControls(
    uiState: PeonyIdentifierState,
    onChampSelected: (String) -> Unit,
    onParcelleSelected: (String) -> Unit,
    onRangSelected: (String) -> Unit,
    onTrouSelected: (String) -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Position",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onReset) {
                    Text("Reset")
                }
            }
            
            // Field (Champ) Dropdown
            DropdownSelector(
                label = "Field",
                selectedValue = uiState.selectedChamp,
                options = uiState.availableChamps,
                onSelectionChanged = onChampSelected,
                enabled = !uiState.isLoading
            )
            
            // Parcel (Parcelle) Dropdown
            DropdownSelector(
                label = "Parcel",
                selectedValue = uiState.selectedParcelle,
                options = uiState.availableParcelles,
                onSelectionChanged = onParcelleSelected,
                enabled = !uiState.isLoading && uiState.selectedChamp != null
            )
            
            // Row (Rang) Dropdown
            DropdownSelector(
                label = "Row",
                selectedValue = uiState.selectedRang,
                options = uiState.availableRangs,
                onSelectionChanged = onRangSelected,
                enabled = !uiState.isLoading && uiState.selectedParcelle != null
            )
            
            // Position (Trou) Dropdown
            DropdownSelector(
                label = "Position",
                selectedValue = uiState.selectedTrou,
                options = uiState.availableTrous,
                onSelectionChanged = onTrouSelected,
                enabled = !uiState.isLoading && uiState.selectedRang != null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
    label: String,
    selectedValue: String?,
    options: List<String>,
    onSelectionChanged: (String) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedValue ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Select $label") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = enabled)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectionChanged(option)
                        expanded = false
                    }
                )
            }
        }
    }
}