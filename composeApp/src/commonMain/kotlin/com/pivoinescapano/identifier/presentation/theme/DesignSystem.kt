package com.pivoinescapano.identifier.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

// Enhanced Color Palette - Botanical Theme
object AppColors {
    // Primary botanical green palette
    val PrimaryGreen = Color(0xFF2E7D32) // Rich botanical green

    val PrimaryContainer = Color(0xFFE8F5E8) // Very light green container

    val SurfaceContainer = Color(0xFFF8FBF8) // Softer green-tinted container

    val BarColor = Color(0xFFB1CCB1) // Softer green-tinted container

    // Improved text colors with better contrast
    val OnSurface = Color(0xFF1A1C1A) // Deep charcoal with green undertone
    val OnSurfaceVariant = Color(0xFF424742) // Medium contrast text
    val OnPrimary = Color(0xFFFFFFFF) // White on primary
    val OnPrimaryContainer = Color(0xFF0E2A11) // Dark text on light primary

    // Enhanced semantic colors
    val ExactMatch = Color(0xFF2E7D32) // Primary green for exact matches
    val Error = Color(0xFFD32F2F) // Material red error
    val Success = Color(0xFF388E3C) // Success green
    val Warning = Color(0xFFFF9800) // Material orange warning
    val Info = Color(0xFF1976D2) // Material blue for notes/info

    // Overlay and feedback colors
    val ScrollOverlay = Color(0xE6000000) // Enhanced overlay opacity

    // Status and utility colors
    val OutlineVariant = Color(0xFFC4C8C1) // Light outline variant
}

// Enhanced Typography Scale with Improved Hierarchy
object AppTypography {
    // Headline styles with improved readability
    val HeadlineLarge =
        TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 36.sp,
            letterSpacing = 0.sp,
        )
    val HeadlineMedium =
        TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 32.sp,
            letterSpacing = 0.sp,
        )
    val HeadlineSmall =
        TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
            letterSpacing = 0.15.sp,
        )

    // Body text with optimized line heights
    val BodyLarge =
        TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp,
        )
    val BodyMedium =
        TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
        )
    val BodySmall =
        TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
        )
    val LabelMedium =
        TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        )
    val LabelSmall =
        TextStyle(
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        )

    // v1.3 Enhanced Bottom Bar Typography - 30% larger than BodyMedium
    val BottomBarLarge =
        TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
            letterSpacing = 0.15.sp,
        )

    // v1.3 Field Selection Screen Typography
    val FieldSelectionTitle =
        TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 30.sp,
            letterSpacing = 0.sp,
        )
    val FieldSelectionDropdown =
        TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 26.sp,
            letterSpacing = 0.1.sp,
        )
}

// Enhanced Spacing System - Golden Ratio Based
object AppSpacing {
    // Base spacing scale using golden ratio (1.618)
    val XS = 4.dp // Base unit
    val S = 8.dp // XS * 2
    val M = 12.dp // Base medium
    val L = 16.dp // Standard spacing
    val XL = 24.dp // L * 1.5
    val Huge = 64.dp // L * 4

    // Component-specific spacing
    val CardPadding = L // 16dp standard card padding
    val CardSpacing = M // 12dp internal card spacing
    val EdgePadding = L // 16dp screen edge padding

    // v1.3 Enhanced Bottom Bar Sizing
    val LargeBottomBarHeight = 64.dp // Enhanced bottom bar height (33% larger)
    val FieldSelectionPadding = 24.dp // Generous padding for field selection screen
    val FieldSelectionDropdownHeight = 64.dp // Large dropdown height for field selection

    // Visual elements
    val OverlayCardSize = 120.dp // Scroll position overlay

    // Border radius system
    val RadiusL = 16.dp // Large radius
    val RadiusXL = 20.dp // Extra large radius
}

// Enhanced Animation Constants
object AppAnimations {
    // Timing constants
    const val SCROLL_OVERLAY_DELAY_MS = 500L
}

// Enhanced Uniform Card Component
@Composable
fun UniformCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
    backgroundColor: Color = AppColors.SurfaceContainer,
    contentColor: Color = AppColors.OnSurface,
    clickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier =
            modifier.then(
                if (clickable && onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                },
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = elevation,
                hoveredElevation = if (clickable) elevation + 2.dp else elevation,
                pressedElevation = if (clickable) elevation + 4.dp else elevation,
            ),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            ),
        shape = RoundedCornerShape(AppSpacing.RadiusL),
        border =
            if (clickable) {
                BorderStroke(1.dp, AppColors.OutlineVariant)
            } else {
                null
            },
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.CardPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.CardSpacing),
            content = content,
        )
    }
}

// Enhanced Overlay Card for Scroll Position
@Composable
fun OverlayCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppColors.ScrollOverlay,
    contentColor: Color = AppColors.OnPrimary,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            ),
        shape = RoundedCornerShape(AppSpacing.RadiusXL),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = AppSpacing.L,
                        vertical = AppSpacing.M,
                    ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}

@Composable
fun FieldSelectionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .let { if (onClick != null) it.clickable { onClick() } else it },
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = if (selected) 6.dp else 2.dp,
                hoveredElevation = if (selected) 8.dp else 4.dp,
                pressedElevation = if (selected) 10.dp else 6.dp,
            ),
        colors =
            CardDefaults.cardColors(
                containerColor = if (selected) AppColors.PrimaryContainer else AppColors.SurfaceContainer,
                contentColor = if (selected) AppColors.OnPrimaryContainer else AppColors.OnSurface,
            ),
        shape = RoundedCornerShape(AppSpacing.RadiusL),
        border = BorderStroke(1.dp, AppColors.PrimaryGreen.copy(alpha = 0.6f)),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.FieldSelectionPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
        ) {
            Text(
                text = title,
                style = AppTypography.FieldSelectionTitle,
                color = if (selected) AppColors.OnPrimaryContainer else AppColors.OnSurface,
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = AppTypography.BodyMedium,
                    color =
                        if (selected) {
                            AppColors.OnPrimaryContainer.copy(
                                alpha = 0.7f,
                            )
                        } else {
                            AppColors.OnSurfaceVariant
                        },
                )
            }

            content()
        }
    }
}

@Composable
fun EnhancedBottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppColors.BarColor,
        shadowElevation = 8.dp,
        tonalElevation = 3.dp,
    ) {
        Box(
            modifier =
                Modifier.padding(
                    bottom =
                        with(LocalDensity.current) {
                            WindowInsets.navigationBars.getBottom(this).toDp()
                        },
                ),
        ) {
            Row(
                modifier =
                    Modifier
                        .height(AppSpacing.LargeBottomBarHeight)
                        .padding(horizontal = AppSpacing.EdgePadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

@Composable
fun HomeTileCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable { onClick() },
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 4.dp,
                hoveredElevation = 8.dp,
                pressedElevation = 2.dp,
            ),
        colors =
            CardDefaults.cardColors(
                containerColor = AppColors.PrimaryContainer,
                contentColor = AppColors.OnPrimaryContainer,
            ),
        shape = RoundedCornerShape(AppSpacing.RadiusL),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(AppSpacing.L),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.M),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.padding(end = AppSpacing.S),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    style = AppTypography.HeadlineSmall,
                    color = AppColors.OnPrimaryContainer,
                )
                Text(
                    text = description,
                    style = AppTypography.BodyMedium,
                    color = AppColors.OnSurfaceVariant,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewOverlayCard() {
    OverlayCard(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        content = {
            Text(
                text = "Scroll Position",
                style = AppTypography.HeadlineMedium,
                color = AppColors.OnPrimary,
            )
            Text(
                text = "This card overlays the scroll position.",
                style = AppTypography.BodyMedium,
                color = AppColors.OnPrimary,
            )
        },
    )
}
