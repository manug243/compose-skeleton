package io.github.manug243.composeskeleton

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors used to draw a skeleton placeholder.
 *
 * Pass an instance to [Modifier.skeleton][skeleton] to override the host colors for a single
 * element. Use [copy] to derive a variant.
 *
 * @param baseColor Color of the placeholder. Must not be [Color.Unspecified].
 * @param highlightColor Color at the center of the shimmer highlight. Must not be
 * [Color.Unspecified].
 * @throws IllegalArgumentException if a color is [Color.Unspecified].
 */
@Immutable
public class SkeletonColors(
    baseColor: Color,
    highlightColor: Color,
) {
    /** Color of the placeholder. */
    public val baseColor: Color = baseColor

    /** Color at the center of the shimmer highlight. */
    public val highlightColor: Color = highlightColor

    init {
        require(baseColor != Color.Unspecified) { "baseColor must be specified." }
        require(highlightColor != Color.Unspecified) { "highlightColor must be specified." }
    }

    /**
     * Returns a copy of these colors with the given values replaced. The same validation as the
     * constructor applies.
     *
     * @param baseColor Color of the placeholder.
     * @param highlightColor Color at the center of the shimmer highlight.
     */
    public fun copy(
        baseColor: Color = this.baseColor,
        highlightColor: Color = this.highlightColor,
    ): SkeletonColors = SkeletonColors(
        baseColor = baseColor,
        highlightColor = highlightColor,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SkeletonColors) return false
        return baseColor == other.baseColor && highlightColor == other.highlightColor
    }

    override fun hashCode(): Int {
        var result = baseColor.hashCode()
        result = 31 * result + highlightColor.hashCode()
        return result
    }

    override fun toString(): String =
        "SkeletonColors(baseColor=$baseColor, highlightColor=$highlightColor)"
}
