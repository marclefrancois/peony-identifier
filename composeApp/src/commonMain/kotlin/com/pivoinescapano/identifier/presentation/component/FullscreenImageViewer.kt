package com.pivoinescapano.identifier.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pivoinescapano.identifier.presentation.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun FullscreenImageViewer(
    imageUrl: String?,
    contentDescription: String?,
    onDismiss: () -> Unit,
) {
    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val transformableState =
        rememberTransformableState { zoomChange, offsetChange, _ ->
            coroutineScope.launch {
                val newScale = (scale.value * zoomChange).coerceIn(0.5f, 5f)
                scale.snapTo(newScale)

                if (newScale <= 1f) {
                    offsetX.snapTo(0f)
                    offsetY.snapTo(0f)
                } else {
                    offsetX.snapTo(offsetX.value + offsetChange.x)
                    offsetY.snapTo(offsetY.value + offsetChange.y)
                }
            }
        }

    // Animate back to scale 1.0 and center when gesture ends
    LaunchedEffect(transformableState.isTransformInProgress) {
        if (!transformableState.isTransformInProgress) {
            // Gesture just ended - immediately animate back to original state
            coroutineScope.launch {
                launch {
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                    )
                }
                launch {
                    offsetX.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                    )
                }
                launch {
                    offsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
                    )
                }
            }
        }
    }

    if (imageUrl != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = false,
                ),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    // Zoomable image
                    PeonyAsyncImage(
                        imageUrl = imageUrl,
                        contentDescription = contentDescription ?: "Peony image",
                        contentScale = ContentScale.Fit,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale.value,
                                    scaleY = scale.value,
                                    translationX = offsetX.value,
                                    translationY = offsetY.value,
                                )
                                .transformable(state = transformableState),
                    )

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AppColors.ScrollOverlay),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close fullscreen image",
                            tint = AppColors.OnPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }
    }
}
