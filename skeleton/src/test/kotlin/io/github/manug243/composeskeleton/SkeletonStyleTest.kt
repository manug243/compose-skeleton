package io.github.manug243.composeskeleton

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertThrows
import org.junit.Test

class SkeletonStyleTest {
    @Test
    fun rejectsUnspecifiedColors() {
        assertThrows(IllegalArgumentException::class.java) {
            SkeletonStyle(
                baseColor = Color.Unspecified,
                highlightColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                animationDurationMillis = 1_200,
                highlightWidth = 160.dp,
                tiltDegrees = 20f,
                direction = SkeletonDirection.LeftToRight,
            )
        }
    }

    @Test
    fun rejectsNonFiniteHighlightWidth() {
        assertThrows(IllegalArgumentException::class.java) {
            SkeletonStyle(
                baseColor = Color.Black,
                highlightColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                animationDurationMillis = 1_200,
                highlightWidth = Float.POSITIVE_INFINITY.dp,
                tiltDegrees = 20f,
                direction = SkeletonDirection.LeftToRight,
            )
        }
    }
}
