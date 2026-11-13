package io.github.manug243.composeskeleton

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SkeletonStyleTest {
    @Test
    fun rejectsUnspecifiedColors() {
        assertFailsWith<IllegalArgumentException> {
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
        assertFailsWith<IllegalArgumentException> {
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

    @Test
    fun equalStylesAreEqualAndHaveSameHashCode() {
        val shape = RoundedCornerShape(8.dp)
        assertEquals(style(shape = shape), style(shape = shape))
        assertEquals(style(shape = shape).hashCode(), style(shape = shape).hashCode())
    }

    @Test
    fun styleDiffersWhenAnyPropertyDiffers() {
        val shape = RoundedCornerShape(8.dp)
        val base = style(shape = shape)
        assertNotEquals(base, base.copy(baseColor = Color.Red))
        assertNotEquals(base, base.copy(highlightColor = Color.Red))
        assertNotEquals(base, base.copy(shape = RoundedCornerShape(4.dp)))
        assertNotEquals(base, base.copy(animationDurationMillis = 500))
        assertNotEquals(base, base.copy(highlightWidth = 80.dp))
        assertNotEquals(base, base.copy(tiltDegrees = 0f))
        assertNotEquals(base, base.copy(direction = SkeletonDirection.TopToBottom))
    }

    @Test
    fun styleCopyReplacesOnlyGivenValues() {
        val base = style()
        val copy = base.copy(baseColor = Color.Red, direction = SkeletonDirection.BottomToTop)

        assertEquals(Color.Red, copy.baseColor)
        assertEquals(SkeletonDirection.BottomToTop, copy.direction)
        assertEquals(base.highlightColor, copy.highlightColor)
        assertEquals(base.shape, copy.shape)
        assertEquals(base.animationDurationMillis, copy.animationDurationMillis)
        assertEquals(base.highlightWidth, copy.highlightWidth)
        assertEquals(base.tiltDegrees, copy.tiltDegrees)
        assertEquals(base, base.copy())
    }

    @Test
    fun styleCopyValidates() {
        assertFailsWith<IllegalArgumentException> { style().copy(animationDurationMillis = 0) }
        assertFailsWith<IllegalArgumentException> { style().copy(tiltDegrees = Float.NaN) }
    }

    @Test
    fun styleToStringListsProperties() {
        val text = style().toString()
        assertTrue(text.startsWith("SkeletonStyle("), text)
        assertTrue("animationDurationMillis=1200" in text, text)
        assertTrue("direction=LeftToRight" in text, text)
    }

    @Test
    fun colorsEqualsHashCodeAndCopy() {
        val colors = SkeletonColors(baseColor = Color.Black, highlightColor = Color.White)

        assertEquals(SkeletonColors(Color.Black, Color.White), colors)
        assertEquals(SkeletonColors(Color.Black, Color.White).hashCode(), colors.hashCode())
        assertNotEquals(colors, colors.copy(baseColor = Color.Red))
        assertNotEquals(colors, colors.copy(highlightColor = Color.Red))
        assertEquals(Color.Red, colors.copy(baseColor = Color.Red).baseColor)
        assertEquals(Color.White, colors.copy(baseColor = Color.Red).highlightColor)
        assertEquals(colors, colors.copy())
        assertTrue(colors.toString().startsWith("SkeletonColors(baseColor="))
    }

    @Test
    fun colorsRejectUnspecified() {
        assertFailsWith<IllegalArgumentException> { SkeletonColors(Color.Unspecified, Color.White) }
        assertFailsWith<IllegalArgumentException> {
            SkeletonColors(Color.Black, Color.White).copy(highlightColor = Color.Unspecified)
        }
    }

    @Test
    fun modeToStringIsReadable() {
        assertEquals("Shimmer", SkeletonMode.Shimmer.toString())
        assertEquals("Solid", SkeletonMode.Solid.toString())
        assertEquals("Keep", SkeletonMode.Keep.toString())
        assertEquals("Hide", SkeletonMode.Hide.toString())
        assertNotEquals(SkeletonMode.Shimmer, SkeletonMode.Solid)
    }

    private fun style(shape: Shape = RoundedCornerShape(8.dp)) = SkeletonStyle(
        baseColor = Color.Black,
        highlightColor = Color.White,
        shape = shape,
        animationDurationMillis = 1_200,
        highlightWidth = 160.dp,
        tiltDegrees = 20f,
        direction = SkeletonDirection.LeftToRight,
    )
}
