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
import com.pivoinescapano.identifier.presentation.component.PeonyAsyncImage
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
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {
        // Compact selection controls at top
        SelectionControls(
            uiState = uiState,
            onChampSelected = viewModel::onChampSelected,
            onParcelleSelected = viewModel::onParcelleSelected,
            onRangSelected = viewModel::onRangSelected,
            onTrouSelected = viewModel::onTrouSelected,
            onReset = viewModel::reset,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Main content area for peony details - takes most space
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 8.dp)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error ?: "Unknown error occurred",
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
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Variety: ${entry.variete ?: "Unknown"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Only show additional info if available
            val additionalInfo = buildList {
                entry.annee_plantation?.let { add("Planted: $it") }
                entry.taille?.let { add("Size: $it") }
            }
            
            if (additionalInfo.isNotEmpty()) {
                additionalInfo.forEach { info ->
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Image and info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Peony image
                PeonyAsyncImage(
                    imageUrl = peony.image,
                    contentDescription = "Image of ${peony.cultivar}",
                    modifier = Modifier.size(120.dp)
                )
                
                // Peony details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Originator: ${peony.originator}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Date: ${peony.date}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Group: ${peony.group}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            if (peony.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Description:",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
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
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header row with title and reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Position",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = onReset,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            // Compact 2x2 grid of dropdowns
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Field and Parcel on first row
                    CompactDropdownSelector(
                        label = "Field",
                        selectedValue = uiState.selectedChamp,
                        options = uiState.availableChamps,
                        onSelectionChanged = onChampSelected,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    )
                    CompactDropdownSelector(
                        label = "Parcel",
                        selectedValue = uiState.selectedParcelle,
                        options = uiState.availableParcelles,
                        onSelectionChanged = onParcelleSelected,
                        enabled = !uiState.isLoading && uiState.selectedChamp != null,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row and Position on second row
                    CompactDropdownSelector(
                        label = "Row",
                        selectedValue = uiState.selectedRang,
                        options = uiState.availableRangs,
                        onSelectionChanged = onRangSelected,
                        enabled = !uiState.isLoading && uiState.selectedParcelle != null,
                        modifier = Modifier.weight(1f)
                    )
                    CompactDropdownSelector(
                        label = "Position",
                        selectedValue = uiState.selectedTrou,
                        options = uiState.availableTrous,
                        onSelectionChanged = onTrouSelected,
                        enabled = !uiState.isLoading && uiState.selectedRang != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactDropdownSelector(
    label: String,
    selectedValue: String?,
    options: List<String>,
    onSelectionChanged: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue ?: "",
            onValueChange = {},
            readOnly = true,
            label = { 
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall
                ) 
            },
            placeholder = { 
                Text(
                    text = "--",
                    style = MaterialTheme.typography.bodySmall
                ) 
            },
            trailingIcon = { 
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) 
            },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = enabled),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            ),
            shape = MaterialTheme.shapes.small
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
                            style = MaterialTheme.typography.bodyMedium
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