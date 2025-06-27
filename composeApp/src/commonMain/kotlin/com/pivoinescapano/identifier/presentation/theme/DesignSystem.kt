package com.pivoinescapano.identifier.presentation.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Palette
object AppColors {
    val PrimaryPurple = Color(0xFF6750A4)
    val PrimaryLight = Color(0xFF9A82DB)
    val PrimaryDark = Color(0xFF4F378B)
    
    val Surface = Color(0xFFFFFBFE)
    val SurfaceVariant = Color(0xFFE7E0EC)
    val SurfaceContainer = Color(0xFFF7F2FA) // Lighter for better contrast
    val SurfaceContainerHigh = Color(0xFFECE6F0)
    
    val OnSurface = Color(0xFF1C1B1F)
    val OnSurfaceVariant = Color(0xFF49454E) // Slightly darker for better readability
    val OnPrimary = Color(0xFFFFFFFF)
    
    val ExactMatch = Color(0xFF4CAF50)
    val FuzzyMatch = Color(0xFFFF9800)
    val Error = Color(0xFFF44336)
    
    val ScrollOverlay = Color(0xCC000000) // Semi-transparent black
    
    // Interactive states
    val InteractiveHover = Color(0xFFF0EBFE)
    val InteractiveBorder = Color(0xFFCAC4D0)
}

// Typography Scale
object AppTypography {
    val HeadlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
    val HeadlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
    val HeadlineSmall = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium)
    
    val BodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
    val BodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
    val BodySmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
    
    val LabelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
    val LabelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val LabelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
}

// Spacing System
object AppSpacing {
    val XS = 4.dp
    val S = 8.dp
    val M = 16.dp
    val L = 24.dp
    val XL = 32.dp
    val XXL = 48.dp
    
    val CardPadding = M
    val CardSpacing = S
    val SelectorHeight = 56.dp
    val BottomSelectorHeight = 48.dp
    val OverlayCardSize = 120.dp
    val CardCornerRadius = 16.dp
}

// Uniform Card Component
@Composable
fun UniformCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    backgroundColor: Color = AppColors.SurfaceContainer,
    contentColor: Color = AppColors.OnSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(AppSpacing.CardCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.CardPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.CardSpacing),
            content = content
        )
    }
}

// Compact Card for Lists
@Composable
fun CompactCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
    backgroundColor: Color = AppColors.SurfaceContainer,
    contentColor: Color = AppColors.OnSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(AppSpacing.S)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.S),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.XS),
            content = content
        )
    }
}

// Overlay Card for Scroll Position
@Composable
fun OverlayCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.ScrollOverlay,
            contentColor = AppColors.OnPrimary
        ),
        shape = RoundedCornerShape(AppSpacing.M)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.M),
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}