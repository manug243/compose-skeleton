package io.github.manug243.composeskeleton.internal

import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import io.github.manug243.composeskeleton.SkeletonStyle

internal data class SkeletonScopeState(
    val enabled: Boolean,
    val style: SkeletonStyle,
    val animationEnabled: Boolean,
    val progress: State<Float>,
    val hostBounds: Rect,
)

internal val LocalSkeletonScope = staticCompositionLocalOf<SkeletonScopeState?> { null }
