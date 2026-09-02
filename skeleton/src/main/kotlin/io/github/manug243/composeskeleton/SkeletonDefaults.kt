package io.github.manug243.composeskeleton

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public object SkeletonDefaults {
    public val Shape: Shape = RoundedCornerShape(8.dp)
    public val HighlightWidth: Dp = 160.dp
    public const val AnimationDurationMillis: Int = 1_200
    public const val TiltDegrees: Float = 20f

    @Composable
    public fun style(
        baseColor: Color = Color.Unspecified,
        highlightColor: Color = Color.Unspecified,
        shape: Shape = Shape,
        animationDurationMillis: Int = AnimationDurationMillis,
        highlightWidth: Dp = HighlightWidth,
        tiltDegrees: Float = TiltDegrees,
        direction: SkeletonDirection = SkeletonDirection.LeftToRight,
    ): SkeletonStyle {
        val darkTheme = isSystemInDarkTheme()
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
