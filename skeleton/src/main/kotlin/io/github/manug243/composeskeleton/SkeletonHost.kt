package io.github.manug243.composeskeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import io.github.manug243.composeskeleton.internal.LocalSkeletonScope
import io.github.manug243.composeskeleton.internal.SkeletonScopeState

@Composable
public fun SkeletonHost(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    style: SkeletonStyle = SkeletonDefaults.style(),
    animationEnabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    var hostBounds by remember { mutableStateOf(Rect.Zero) }
    val shouldAnimate = enabled && animationEnabled
    val progress = if (shouldAnimate) {
        val transition = rememberInfiniteTransition(label = "Skeleton shimmer")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = style.animationDurationMillis,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart,
            ),
            label = "Skeleton shimmer progress",
        )
    } else {
        rememberUpdatedState(0f)
    }
    val scopeState = remember(enabled, style, animationEnabled, progress, hostBounds) {
        SkeletonScopeState(
            enabled = enabled,
            style = style,
            animationEnabled = animationEnabled,
            progress = progress,
            hostBounds = hostBounds,
        )
    }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            hostBounds = coordinates.boundsInWindow()
        },
        propagateMinConstraints = true,
    ) {
        CompositionLocalProvider(LocalSkeletonScope provides scopeState) {
            content()
        }
    }
}
