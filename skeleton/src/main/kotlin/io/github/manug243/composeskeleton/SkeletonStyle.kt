package io.github.manug243.composeskeleton

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * Visual settings shared by all placeholders of a [SkeletonHost].
 *
 * Create one with [SkeletonDefaults.style].
 *
 * @property baseColor Color of the placeholder.
 * @property highlightColor Color at the center of the shimmer highlight.
 * @property shape Default shape of placeholders.
 * @property animationDurationMillis Duration of one shimmer sweep, greater than zero.
 * @property highlightWidth Width of the shimmer highlight, greater than zero.
 * @property tiltDegrees Extra rotation of the shimmer direction in degrees.
 * @property direction Direction in which the highlight travels.
 */
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
