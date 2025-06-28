package com.pivoinescapano.identifier.presentation.component.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.pivoinescapano.identifier.presentation.theme.AppColors
import com.pivoinescapano.identifier.presentation.theme.AppSpacing
import com.pivoinescapano.identifier.presentation.theme.AppTypography
import com.pivoinescapano.identifier.presentation.theme.UniformCard

@Composable
fun ErrorContent(
    error: String,
    onDismiss: () -> Unit,
) {
    UniformCard(
        modifier = Modifier.padding(AppSpacing.M),
        backgroundColor = AppColors.Error.copy(alpha = 0.1f),
        contentColor = AppColors.Error,
    ) {
        Text(
            text = "Error",
            style = AppTypography.HeadlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = error,
            style = AppTypography.BodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Dismiss")
        }
    }
}
