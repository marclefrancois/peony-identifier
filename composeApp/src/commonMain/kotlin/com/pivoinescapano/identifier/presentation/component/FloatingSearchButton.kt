package com.pivoinescapano.identifier.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pivoinescapano.identifier.presentation.theme.AppColors

@Composable
fun FloatingSearchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        shape = CircleShape,
        onClick = onClick,
        containerColor = AppColors.Error,
        contentColor = AppColors.OnPrimary,
        modifier = modifier.size(64.dp),
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