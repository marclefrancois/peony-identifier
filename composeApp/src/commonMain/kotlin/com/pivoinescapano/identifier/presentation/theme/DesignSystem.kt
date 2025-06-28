package com.pivoinescapano.identifier.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Enhanced Color Palette - Botanical Theme
object AppColors {
    // Primary botanical green palette
    val PrimaryGreen = Color(0xFF2E7D32)      // Rich botanical green
    val PrimaryLight = Color(0xFF66BB6A)      // Light sage green  
    val PrimaryDark = Color(0xFF1B5E20)       // Deep forest green
    val PrimaryContainer = Color(0xFFE8F5E8)  // Very light green container
    
    // Enhanced surface colors with botanical undertones
    val Surface = Color(0xFFFFFFFE)           // Pure white
    val SurfaceVariant = Color(0xFFF1F4F1)    // Light green-tinted surface
    val SurfaceContainer = Color(0xFFF8FBF8)  // Softer green-tinted container
    val SurfaceContainerHigh = Color(0xFFEDF2ED) // Higher contrast green surface
    val SurfaceContainerHighest = Color(0xFFE1E8E1) // Highest contrast surface
    
    // Improved text colors with better contrast
    val OnSurface = Color(0xFF1A1C1A)         // Deep charcoal with green undertone
    val OnSurfaceVariant = Color(0xFF424742)  // Medium contrast text
    val OnPrimary = Color(0xFFFFFFFF)         // White on primary
    val OnPrimaryContainer = Color(0xFF0E2A11) // Dark text on light primary
    
    // Enhanced semantic colors
    val ExactMatch = Color(0xFF2E7D32)        // Primary green for exact matches
    val FuzzyMatch = Color(0xFFED6C02)        // Warm orange for suggestions
    val Warning = Color(0xFFEF6C00)           // Amber warning
    val Error = Color(0xFFD32F2F)             // Material red error
    val Success = Color(0xFF388E3C)           // Success green
    
    // Overlay and feedback colors
    val ScrollOverlay = Color(0xE6000000)     // Enhanced overlay opacity
    val LoadingOverlay = Color(0x80FFFFFF)    // Light overlay for loading states
    
    // Interactive states with botanical theme
    val InteractiveHover = Color(0xFFE8F5E8)  // Light green hover
    val InteractiveFocus = Color(0xFFDCE7DC) // Medium green focus
    val InteractiveBorder = Color(0xFFBDBDBD) // Neutral border
    val InteractivePressed = Color(0xFFCCE4CC) // Pressed state
    
    // Status and utility colors
    val Outline = Color(0xFF75796F)           // Subtle outline
    val OutlineVariant = Color(0xFFC4C8C1)    // Light outline variant
    val Scrim = Color(0x66000000)             // Modal scrim
    val Shadow = Color(0x1A000000)            // Soft shadow color
}

// Enhanced Typography Scale with Improved Hierarchy
object AppTypography {
    // Display styles for major headings
    val DisplayLarge = TextStyle(
        fontSize = 36.sp, 
        fontWeight = FontWeight.Bold,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
    )
    val DisplayMedium = TextStyle(
        fontSize = 32.sp, 
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )
    
    // Headline styles with improved readability
    val HeadlineLarge = TextStyle(
        fontSize = 28.sp, 
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
    val HeadlineMedium = TextStyle(
        fontSize = 24.sp, 
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )
    val HeadlineSmall = TextStyle(
        fontSize = 20.sp, 
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    )
    
    // Body text with optimized line heights
    val BodyLarge = TextStyle(
        fontSize = 16.sp, 
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )
    val BodyMedium = TextStyle(
        fontSize = 14.sp, 
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    val BodySmall = TextStyle(
        fontSize = 12.sp, 
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    
    // Label styles for UI elements
    val LabelLarge = TextStyle(
        fontSize = 14.sp, 
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    val LabelMedium = TextStyle(
        fontSize = 12.sp, 
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    val LabelSmall = TextStyle(
        fontSize = 11.sp, 
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    
    // Specialized styles
    val Caption = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    )
    val Overline = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp
    )
    
    // v1.3 Enhanced Bottom Bar Typography - 30% larger than BodyMedium
    val BottomBarLarge = TextStyle(
        fontSize = 20.sp,      // 30% larger than BodyMedium (14sp)
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    )
    
    // v1.3 Field Selection Screen Typography
    val FieldSelectionTitle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    )
    val FieldSelectionDropdown = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 26.sp,
        letterSpacing = 0.1.sp
    )
}

// Enhanced Spacing System - Golden Ratio Based
object AppSpacing {
    // Base spacing scale using golden ratio (1.618)
    val XXS = 2.dp     // Minimal spacing
    val XS = 4.dp      // Base unit
    val S = 8.dp       // XS * 2
    val M = 12.dp      // Base medium
    val L = 16.dp      // Standard spacing
    val XL = 24.dp     // L * 1.5
    val XXL = 32.dp    // L * 2
    val XXXL = 48.dp   // L * 3
    val Huge = 64.dp   // L * 4
    
    // Component-specific spacing
    val CardPadding = L              // 16dp standard card padding
    val CardSpacing = M              // 12dp internal card spacing
    val ComponentSpacing = S         // 8dp between related components
    val SectionSpacing = XL          // 24dp between major sections
    val EdgePadding = L              // 16dp screen edge padding
    
    // Interactive element sizing
    val MinTouchTarget = 48.dp       // Minimum touch target size
    val SelectorHeight = 56.dp       // Standard dropdown height
    val BottomSelectorHeight = 48.dp // Bottom navigation height
    val ButtonHeight = 40.dp         // Standard button height
    val CompactButtonHeight = 32.dp  // Compact button height
    
    // v1.3 Enhanced Bottom Bar Sizing
    val LargeBottomBarHeight = 64.dp // Enhanced bottom bar height (33% larger)
    val FieldSelectionPadding = 24.dp // Generous padding for field selection screen
    val FieldSelectionDropdownHeight = 64.dp // Large dropdown height for field selection
    val ContinueButtonHeight = 52.dp // Prominent continue button
    
    // Visual elements
    val OverlayCardSize = 120.dp     // Scroll position overlay
    val ImageSize = 120.dp           // Standard image display size
    val IconSize = 24.dp             // Standard icon size
    val SmallIconSize = 16.dp        // Small icon size
    
    // Border radius system
    val RadiusXS = 4.dp              // Small radius
    val RadiusS = 8.dp               // Medium radius
    val RadiusM = 12.dp              // Standard radius
    val RadiusL = 16.dp              // Large radius
    val RadiusXL = 20.dp             // Extra large radius
    val RadiusRound = 50.dp          // Fully rounded
    
    // Legacy aliases for backward compatibility
    val CardCornerRadius = RadiusL    // 16dp for cards
}

// Enhanced Animation Constants
object AppAnimations {
    // Timing constants
    const val SCROLL_OVERLAY_DELAY_MS = 500L
    const val MICRO_INTERACTION_DURATION_MS = 150L
    const val STANDARD_DURATION_MS = 300L
    const val EMPHASIZED_DURATION_MS = 500L
    
    // Easing values
    const val STANDARD_EASING_DECELERATE = 0.4f
    const val EMPHASIZED_EASING_DECELERATE = 0.2f
    
    // Hover and press animations
    const val HOVER_SCALE = 1.02f
    const val PRESS_SCALE = 0.98f
    const val HOVER_ALPHA = 0.08f
    const val PRESS_ALPHA = 0.12f
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
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.then(
            if (clickable && onClick != null) {
                Modifier.clickable { onClick() }
            } else Modifier
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            hoveredElevation = if (clickable) elevation + 2.dp else elevation,
            pressedElevation = if (clickable) elevation + 4.dp else elevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(AppSpacing.RadiusL),
        border = if (clickable) {
            BorderStroke(1.dp, AppColors.OutlineVariant)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.CardPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.CardSpacing),
            content = content
        )
    }
}

// Enhanced Compact Card for Lists
@Composable
fun CompactCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 1.dp,
    backgroundColor: Color = AppColors.SurfaceContainer,
    contentColor: Color = AppColors.OnSurface,
    clickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.then(
            if (clickable && onClick != null) {
                Modifier.clickable { onClick() }
            } else Modifier
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            hoveredElevation = if (clickable) elevation + 1.dp else elevation,
            pressedElevation = if (clickable) elevation + 2.dp else elevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AppColors.PrimaryContainer else backgroundColor,
            contentColor = if (selected) AppColors.OnPrimaryContainer else contentColor
        ),
        shape = RoundedCornerShape(AppSpacing.RadiusM),
        border = if (selected) {
            BorderStroke(2.dp, AppColors.PrimaryGreen)
        } else if (clickable) {
            BorderStroke(1.dp, AppColors.OutlineVariant)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = AppSpacing.M,
                vertical = AppSpacing.ComponentSpacing
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.XS),
            content = content
        )
    }
}

// Enhanced Overlay Card for Scroll Position
@Composable
fun OverlayCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppColors.ScrollOverlay,
    contentColor: Color = AppColors.OnPrimary,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(AppSpacing.RadiusXL)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = AppSpacing.L,
                vertical = AppSpacing.M
            ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

// New Status Card for Match Results
@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    status: MatchStatus,
    content: @Composable ColumnScope.() -> Unit
) {
    val backgroundColor = when (status) {
        MatchStatus.ExactMatch -> AppColors.Success.copy(alpha = 0.1f)
        MatchStatus.FuzzyMatch -> AppColors.Warning.copy(alpha = 0.1f)
        MatchStatus.NoMatch -> AppColors.Error.copy(alpha = 0.1f)
        MatchStatus.Loading -> AppColors.SurfaceVariant
    }
    
    val borderColor = when (status) {
        MatchStatus.ExactMatch -> AppColors.Success
        MatchStatus.FuzzyMatch -> AppColors.Warning
        MatchStatus.NoMatch -> AppColors.Error
        MatchStatus.Loading -> AppColors.OutlineVariant
    }
    
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = AppColors.OnSurface
        ),
        shape = RoundedCornerShape(AppSpacing.RadiusM),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.CardPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.ComponentSpacing),
            content = content
        )
    }
}

// Match Status Enum
enum class MatchStatus {
    ExactMatch,
    FuzzyMatch,
    NoMatch,
    Loading
}

// v1.3 Enhanced Field Selection Card
@Composable
fun FieldSelectionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    selected: Boolean = false,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 4.dp else 2.dp,
            hoveredElevation = if (selected) 6.dp else 4.dp,
            pressedElevation = if (selected) 8.dp else 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AppColors.PrimaryContainer else AppColors.SurfaceContainer,
            contentColor = if (selected) AppColors.OnPrimaryContainer else AppColors.OnSurface
        ),
        shape = RoundedCornerShape(AppSpacing.RadiusL),
        border = if (selected) {
            BorderStroke(2.dp, AppColors.PrimaryGreen)
        } else {
            BorderStroke(1.dp, AppColors.OutlineVariant)
        }
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.FieldSelectionPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
        ) {
            Text(
                text = title,
                style = AppTypography.FieldSelectionTitle,
                color = if (selected) AppColors.OnPrimaryContainer else AppColors.OnSurface
            )
            
            subtitle?.let {
                Text(
                    text = it,
                    style = AppTypography.BodyMedium,
                    color = if (selected) AppColors.OnPrimaryContainer.copy(alpha = 0.7f) else AppColors.OnSurfaceVariant
                )
            }
            
            content()
        }
    }
}

// v1.3 Enhanced Bottom Navigation Bar for Row Selection
@Composable
fun EnhancedBottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppColors.SurfaceContainer,
        shadowElevation = 8.dp,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .height(AppSpacing.LargeBottomBarHeight)
                .padding(horizontal = AppSpacing.EdgePadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

// v1.3 Continue Button Component
@Composable
fun ContinueButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    text: String = "Continue"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(AppSpacing.ContinueButtonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.PrimaryGreen,
            contentColor = AppColors.OnPrimary,
            disabledContainerColor = AppColors.OutlineVariant,
            disabledContentColor = AppColors.OnSurfaceVariant
        ),
        shape = RoundedCornerShape(AppSpacing.RadiusL),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = AppTypography.LabelLarge.copy(fontSize = 16.sp),
            fontWeight = FontWeight.SemiBold
        )
    }
}