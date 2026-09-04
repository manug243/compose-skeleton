package io.github.manug243.composeskeleton

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import io.github.manug243.composeskeleton.internal.SkeletonElement

public fun Modifier.skeleton(
    mode: SkeletonMode = SkeletonMode.Shimmer,
    shape: Shape? = null,
    colors: SkeletonColors? = null,
): Modifier = this.then(SkeletonElement(mode = mode, shape = shape, colors = colors))
