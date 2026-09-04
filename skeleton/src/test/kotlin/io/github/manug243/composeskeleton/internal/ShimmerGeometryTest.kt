package io.github.manug243.composeskeleton.internal

import androidx.compose.ui.geometry.Rect
import io.github.manug243.composeskeleton.SkeletonDirection
import org.junit.Assert.assertEquals
import org.junit.Test

class ShimmerGeometryTest {
    @Test
    fun leftToRight_movesAcrossEntireHost() {
        val host = Rect(left = 0f, top = 0f, right = 100f, bottom = 20f)

        val start = calculateGradientLine(
            hostBounds = host,
            elementBounds = host,
            progress = 0f,
            highlightWidthPx = 20f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.LeftToRight,
        )
        val middle = calculateGradientLine(
            hostBounds = host,
            elementBounds = host,
            progress = 0.5f,
            highlightWidthPx = 20f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.LeftToRight,
        )
        val end = calculateGradientLine(
            hostBounds = host,
            elementBounds = host,
            progress = 1f,
            highlightWidthPx = 20f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.LeftToRight,
        )

        assertEquals(-20f, start.start.x, 0.001f)
        assertEquals(0f, start.end.x, 0.001f)
        assertEquals(40f, middle.start.x, 0.001f)
        assertEquals(60f, middle.end.x, 0.001f)
        assertEquals(100f, end.start.x, 0.001f)
        assertEquals(120f, end.end.x, 0.001f)
    }

    @Test
    fun elementsUseSameHostSpace() {
        val host = Rect(left = 0f, top = 0f, right = 200f, bottom = 40f)
        val firstElement = Rect(left = 0f, top = 0f, right = 100f, bottom = 40f)
        val secondElement = Rect(left = 100f, top = 0f, right = 200f, bottom = 40f)

        val firstLine = calculateGradientLine(
            hostBounds = host,
            elementBounds = firstElement,
            progress = 0.5f,
            highlightWidthPx = 40f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.LeftToRight,
        )
        val secondLine = calculateGradientLine(
            hostBounds = host,
            elementBounds = secondElement,
            progress = 0.5f,
            highlightWidthPx = 40f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.LeftToRight,
        )

        assertEquals(firstLine.start.x - 100f, secondLine.start.x, 0.001f)
        assertEquals(firstLine.end.x - 100f, secondLine.end.x, 0.001f)
    }

    @Test
    fun rightToLeft_reversesTravelDirection() {
        val host = Rect(left = 0f, top = 0f, right = 100f, bottom = 20f)

        val start = calculateGradientLine(
            hostBounds = host,
            elementBounds = host,
            progress = 0f,
            highlightWidthPx = 20f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.RightToLeft,
        )
        val end = calculateGradientLine(
            hostBounds = host,
            elementBounds = host,
            progress = 1f,
            highlightWidthPx = 20f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.RightToLeft,
        )

        assertEquals(120f, start.start.x, 0.001f)
        assertEquals(100f, start.end.x, 0.001f)
        assertEquals(0f, end.start.x, 0.001f)
        assertEquals(-20f, end.end.x, 0.001f)
    }

    @Test
    fun topToBottom_movesAcrossEntireHost() {
        val host = Rect(left = 10f, top = 20f, right = 110f, bottom = 120f)

        val start = calculateGradientLine(
            hostBounds = host,
            elementBounds = host,
            progress = 0f,
            highlightWidthPx = 20f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.TopToBottom,
        )
        val end = calculateGradientLine(
            hostBounds = host,
            elementBounds = host,
            progress = 1f,
            highlightWidthPx = 20f,
            tiltDegrees = 0f,
            direction = SkeletonDirection.TopToBottom,
        )

        assertEquals(-20f, start.start.y, 0.001f)
        assertEquals(0f, start.end.y, 0.001f)
        assertEquals(100f, end.start.y, 0.001f)
        assertEquals(120f, end.end.y, 0.001f)
    }
}
