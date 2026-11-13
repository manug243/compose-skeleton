package io.github.manug243.composeskeleton

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Default values and the style factory for [SkeletonHost]. */
public object SkeletonDefaults {
    /** Default placeholder shape: rounded corners with an 8 dp radius. */
    public val Shape: Shape = RoundedCornerShape(8.dp)

    /** Default width of the shimmer highlight. */
    public val HighlightWidth: Dp = 160.dp

    /** Default duration of one shimmer sweep in milliseconds. */
    public const val AnimationDurationMillis: Int = 1_200

    /** Default extra rotation of the shimmer direction in degrees. */
    public const val TiltDegrees: Float = 20f

    /**
     * Creates a [SkeletonStyle].
     *
     * Unspecified colors fall back to a neutral light or dark default, chosen by [darkTheme].
     * Apps with their own theme switch should pass their theme's dark flag instead of relying on
     * the system setting, or provide a complete style once through [LocalSkeletonStyle].
     *
     * @param baseColor Color of the placeholder, or [Color.Unspecified] for the default.
     * @param highlightColor Color at the center of the shimmer highlight, or
     * [Color.Unspecified] for the default.
     * @param shape Default shape of placeholders.
     * @param animationDurationMillis Duration of one shimmer sweep, greater than zero.
     * @param highlightWidth Width of the shimmer highlight, finite and greater than zero.
     * @param tiltDegrees Extra rotation of the shimmer direction in degrees, finite.
     * @param direction Direction in which the highlight travels.
     * @param darkTheme Whether to use the dark default colors for unspecified colors. Defaults
     * to the system setting.
     */
    @Composable
    public fun style(
        baseColor: Color = Color.Unspecified,
        highlightColor: Color = Color.Unspecified,
        shape: Shape = Shape,
        animationDurationMillis: Int = AnimationDurationMillis,
        highlightWidth: Dp = HighlightWidth,
        tiltDegrees: Float = TiltDegrees,
        direction: SkeletonDirection = SkeletonDirection.LeftToRight,
        darkTheme: Boolean = isSystemInDarkTheme(),
    ): SkeletonStyle {
        val resolvedBaseColor = baseColor.takeOrElse {
            if (darkTheme) Color(0xFF30343A) else Color(0xFFE3E6EA)
        }
        val resolvedHighlightColor = highlightColor.takeOrElse {
            if (darkTheme) Color(0xFF494E56) else Color(0xFFF5F6F8)
        }

        return remember(
            resolvedBaseColor,
            resolvedHighlightColor,
            shape,
            animationDurationMillis,
            highlightWidth,
            tiltDegrees,
            direction,
        ) {
            SkeletonStyle(
                baseColor = resolvedBaseColor,
                highlightColor = resolvedHighlightColor,
                shape = shape,
                animationDurationMillis = animationDurationMillis,
                highlightWidth = highlightWidth,
                tiltDegrees = tiltDegrees,
                direction = direction,
            )
        }
    }

    private inline fun Color.takeOrElse(block: () -> Color): Color =
        if (this == Color.Unspecified) block() else this
}
