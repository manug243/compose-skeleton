package io.github.manug243.composeskeleton

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Immutable
public data class SkeletonStyle(
    public val baseColor: Color,
    public val highlightColor: Color,
    public val shape: Shape,
    public val animationDurationMillis: Int,
    public val highlightWidth: Dp,
    public val tiltDegrees: Float,
    public val direction: SkeletonDirection,
) {
    init {
        require(baseColor != Color.Unspecified) { "baseColor must be specified." }
        require(highlightColor != Color.Unspecified) { "highlightColor must be specified." }
        require(animationDurationMillis > 0) { "animationDurationMillis must be greater than zero." }
        require(highlightWidth.value.isFinite() && highlightWidth.value > 0f) {
            "highlightWidth must be finite and greater than zero."
        }
        require(tiltDegrees.isFinite()) { "tiltDegrees must be finite." }
    }
}
