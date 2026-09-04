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

internal class ShimmerGeometry(
    hostBounds: Rect,
    highlightWidthPx: Float,
    tiltDegrees: Float,
    direction: SkeletonDirection,
) {
    private val directionVector: Offset
    private val minimum: Float
    private val maximum: Float
    private val halfWidth = highlightWidthPx / 2f

    init {
        val baseAngle = when (direction) {
            SkeletonDirection.LeftToRight -> 0f
            SkeletonDirection.RightToLeft -> 180f
            SkeletonDirection.TopToBottom -> 90f
            SkeletonDirection.BottomToTop -> 270f
        }
        val angleRadians = (baseAngle + tiltDegrees) * PI.toFloat() / 180f
        directionVector = Offset(cos(angleRadians), sin(angleRadians))

        fun projection(point: Offset): Float =
            point.x * directionVector.x + point.y * directionVector.y

        val topLeft = projection(hostBounds.topLeft)
        val topRight = projection(hostBounds.topRight)
        val bottomLeft = projection(hostBounds.bottomLeft)
        val bottomRight = projection(hostBounds.bottomRight)
        minimum = minOf(topLeft, topRight, bottomLeft, bottomRight)
        maximum = maxOf(topLeft, topRight, bottomLeft, bottomRight)
    }

    fun gradientLine(elementBounds: Rect, progress: Float): GradientLine {
        val center = (minimum - halfWidth) +
            (maximum - minimum + halfWidth * 2f) * progress.coerceIn(0f, 1f)

        fun localPoint(projection: Float): Offset = Offset(
            x = directionVector.x * projection - elementBounds.left,
            y = directionVector.y * projection - elementBounds.top,
        )

        return GradientLine(
            start = localPoint(center - halfWidth),
            end = localPoint(center + halfWidth),
        )
    }
}

internal fun calculateGradientLine(
    hostBounds: Rect,
    elementBounds: Rect,
    progress: Float,
    highlightWidthPx: Float,
    tiltDegrees: Float,
    direction: SkeletonDirection,
): GradientLine {
    return ShimmerGeometry(hostBounds, highlightWidthPx, tiltDegrees, direction)
        .gradientLine(elementBounds, progress)
}
