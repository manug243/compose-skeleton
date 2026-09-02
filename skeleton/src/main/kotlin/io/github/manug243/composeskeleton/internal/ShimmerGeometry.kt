package io.github.manug243.composeskeleton.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.manug243.composeskeleton.SkeletonDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal data class GradientLine(
    val start: Offset,
    val end: Offset,
)

internal fun calculateGradientLine(
    hostBounds: Rect,
    elementBounds: Rect,
    progress: Float,
    highlightWidthPx: Float,
    tiltDegrees: Float,
    direction: SkeletonDirection,
): GradientLine {
    val baseAngle = when (direction) {
        SkeletonDirection.LeftToRight -> 0f
        SkeletonDirection.RightToLeft -> 180f
        SkeletonDirection.TopToBottom -> 90f
        SkeletonDirection.BottomToTop -> 270f
    }
    val angleRadians = (baseAngle + tiltDegrees) * PI.toFloat() / 180f
    val directionVector = Offset(cos(angleRadians), sin(angleRadians))

    fun projection(point: Offset): Float =
        point.x * directionVector.x + point.y * directionVector.y

    val projections = floatArrayOf(
        projection(hostBounds.topLeft),
        projection(hostBounds.topRight),
        projection(hostBounds.bottomLeft),
        projection(hostBounds.bottomRight),
    )
    val minimum = projections.min()
    val maximum = projections.max()
    val halfWidth = highlightWidthPx / 2f
    val clampedProgress = progress.coerceIn(0f, 1f)
    val center = (minimum - halfWidth) +
        (maximum - minimum + highlightWidthPx) * clampedProgress

    fun localPoint(projection: Float): Offset = Offset(
        x = directionVector.x * projection - elementBounds.left,
        y = directionVector.y * projection - elementBounds.top,
    )

    return GradientLine(
        start = localPoint(center - halfWidth),
        end = localPoint(center + halfWidth),
    )
}
