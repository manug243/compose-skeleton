package io.github.manug243.composeskeleton

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import io.github.manug243.composeskeleton.internal.SkeletonElement

/**
 * Marks this element as a skeleton placeholder inside a [SkeletonHost].
 *
 * The placeholder uses the size Compose already measured for the element. Outside a
 * [SkeletonHost], or while the host is disabled, the element is drawn normally.
 *
 * @param mode How the element behaves while the host is enabled.
 * @param shape Overrides [SkeletonStyle.shape] for this element, or `null` to use the host's.
 * @param colors Overrides the host colors for this element, or `null` to use the host's.
 */
public fun Modifier.skeleton(
    mode: SkeletonMode = SkeletonMode.Shimmer,
    shape: Shape? = null,
    colors: SkeletonColors? = null,
): Modifier = this.then(SkeletonElement(mode = mode, shape = shape, colors = colors))
