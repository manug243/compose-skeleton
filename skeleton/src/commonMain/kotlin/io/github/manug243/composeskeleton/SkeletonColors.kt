package io.github.manug243.composeskeleton

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Colors used to draw a skeleton placeholder. */
@Immutable
public data class SkeletonColors(
    public val baseColor: Color,
    public val highlightColor: Color,
) {
    init {
        require(baseColor != Color.Unspecified) { "baseColor must be specified." }
        require(highlightColor != Color.Unspecified) { "highlightColor must be specified." }
    }
}
