package io.github.manug243.composeskeleton

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * Visual settings shared by all placeholders of a [SkeletonHost].
 *
 * Create one with [SkeletonDefaults.style], derive variants with [copy], or provide one for a
 * whole app through [LocalSkeletonStyle].
 *
 * @param baseColor Color of the placeholder. Must not be [Color.Unspecified].
 * @param highlightColor Color at the center of the shimmer highlight. Must not be
 * [Color.Unspecified].
 * @param shape Default shape of placeholders.
 * @param animationDurationMillis Duration of one shimmer sweep, greater than zero.
 * @param highlightWidth Width of the shimmer highlight, finite and greater than zero.
 * @param tiltDegrees Extra rotation of the shimmer direction in degrees, finite.
 * @param direction Direction in which the highlight travels.
 * @throws IllegalArgumentException if any value is outside its documented range.
 */
@Immutable
public class SkeletonStyle(
    baseColor: Color,
    highlightColor: Color,
    shape: Shape,
    animationDurationMillis: Int,
    highlightWidth: Dp,
    tiltDegrees: Float,
    direction: SkeletonDirection,
) {
    /** Color of the placeholder. */
    public val baseColor: Color = baseColor

    /** Color at the center of the shimmer highlight. */
    public val highlightColor: Color = highlightColor

    /** Default shape of placeholders. */
    public val shape: Shape = shape

    /** Duration of one shimmer sweep in milliseconds. */
    public val animationDurationMillis: Int = animationDurationMillis

    /** Width of the shimmer highlight. */
    public val highlightWidth: Dp = highlightWidth

    /** Extra rotation of the shimmer direction in degrees. */
    public val tiltDegrees: Float = tiltDegrees

    /** Direction in which the highlight travels. */
    public val direction: SkeletonDirection = direction

    init {
        require(baseColor != Color.Unspecified) { "baseColor must be specified." }
        require(highlightColor != Color.Unspecified) { "highlightColor must be specified." }
        require(animationDurationMillis > 0) { "animationDurationMillis must be greater than zero." }
        require(highlightWidth.value.isFinite() && highlightWidth.value > 0f) {
            "highlightWidth must be finite and greater than zero."
        }
        require(tiltDegrees.isFinite()) { "tiltDegrees must be finite." }
    }

    /**
     * Returns a copy of this style with the given values replaced. The same validation as the
     * constructor applies.
     *
     * @param baseColor Color of the placeholder.
     * @param highlightColor Color at the center of the shimmer highlight.
     * @param shape Default shape of placeholders.
     * @param animationDurationMillis Duration of one shimmer sweep, greater than zero.
     * @param highlightWidth Width of the shimmer highlight, finite and greater than zero.
     * @param tiltDegrees Extra rotation of the shimmer direction in degrees, finite.
     * @param direction Direction in which the highlight travels.
     */
    public fun copy(
        baseColor: Color = this.baseColor,
        highlightColor: Color = this.highlightColor,
        shape: Shape = this.shape,
        animationDurationMillis: Int = this.animationDurationMillis,
        highlightWidth: Dp = this.highlightWidth,
        tiltDegrees: Float = this.tiltDegrees,
        direction: SkeletonDirection = this.direction,
    ): SkeletonStyle = SkeletonStyle(
        baseColor = baseColor,
        highlightColor = highlightColor,
        shape = shape,
        animationDurationMillis = animationDurationMillis,
        highlightWidth = highlightWidth,
        tiltDegrees = tiltDegrees,
        direction = direction,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SkeletonStyle) return false
        return baseColor == other.baseColor &&
            highlightColor == other.highlightColor &&
            shape == other.shape &&
            animationDurationMillis == other.animationDurationMillis &&
            highlightWidth == other.highlightWidth &&
            tiltDegrees == other.tiltDegrees &&
            direction == other.direction
    }

    override fun hashCode(): Int {
        var result = baseColor.hashCode()
        result = 31 * result + highlightColor.hashCode()
        result = 31 * result + shape.hashCode()
        result = 31 * result + animationDurationMillis
        result = 31 * result + highlightWidth.hashCode()
        result = 31 * result + tiltDegrees.hashCode()
        result = 31 * result + direction.hashCode()
        return result
    }

    override fun toString(): String =
        "SkeletonStyle(" +
            "baseColor=$baseColor, " +
            "highlightColor=$highlightColor, " +
            "shape=$shape, " +
            "animationDurationMillis=$animationDurationMillis, " +
            "highlightWidth=$highlightWidth, " +
            "tiltDegrees=$tiltDegrees, " +
            "direction=$direction" +
            ")"
}
