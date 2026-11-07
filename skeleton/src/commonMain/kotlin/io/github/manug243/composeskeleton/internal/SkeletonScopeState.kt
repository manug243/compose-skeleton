package io.github.manug243.composeskeleton.internal

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import io.github.manug243.composeskeleton.SkeletonStyle

internal class SkeletonScopeState(
    enabled: Boolean,
    style: SkeletonStyle,
    animationEnabled: Boolean,
    progress: State<Float>,
) {
    var enabled by mutableStateOf(enabled)
    var style by mutableStateOf(style)
    var animationEnabled by mutableStateOf(animationEnabled)
    var progress by mutableStateOf(progress)
    var hostBounds by mutableStateOf(Rect.Zero)

    fun update(
        enabled: Boolean,
        style: SkeletonStyle,
        animationEnabled: Boolean,
        progress: State<Float>,
    ) {
        this.enabled = enabled
        this.style = style
        this.animationEnabled = animationEnabled
        this.progress = progress
    }
}

internal val LocalSkeletonScope = staticCompositionLocalOf<SkeletonScopeState?> { null }
