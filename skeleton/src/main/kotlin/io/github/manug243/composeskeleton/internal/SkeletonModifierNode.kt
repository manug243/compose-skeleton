package io.github.manug243.composeskeleton.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import io.github.manug243.composeskeleton.SkeletonMode

internal data class SkeletonElement(
    val mode: SkeletonMode,
    val shape: Shape?,
) : ModifierNodeElement<SkeletonNode>() {
    override fun create(): SkeletonNode = SkeletonNode(mode = mode, shape = shape)

    override fun update(node: SkeletonNode) {
        node.mode = mode
        node.shape = shape
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "skeleton"
        properties["mode"] = mode
        properties["shape"] = shape
    }
}

internal class SkeletonNode(
    var mode: SkeletonMode,
    var shape: Shape?,
) : Modifier.Node(),
    DrawModifierNode,
    GlobalPositionAwareModifierNode,
    CompositionLocalConsumerModifierNode,
    SemanticsModifierNode {

    private var elementBounds: Rect = Rect.Zero

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        elementBounds = coordinates.boundsInWindow()
        invalidateDraw()
    }

    override val shouldClearDescendantSemantics: Boolean
        get() = currentValueOf(LocalSkeletonScope)?.enabled == true && mode != SkeletonMode.Keep

    override fun SemanticsPropertyReceiver.applySemantics() = Unit

    override fun ContentDrawScope.draw() {
        val scope = currentValueOf(LocalSkeletonScope)
        if (scope == null) {
            drawContent()
            return
        }
        when {
            !scope.enabled || mode == SkeletonMode.Keep -> drawContent()
            mode == SkeletonMode.Hide -> Unit
            mode == SkeletonMode.Solid || !scope.animationEnabled -> {
                drawPlaceholder(color = scope.style.baseColor, shape = shape ?: scope.style.shape)
            }
            else -> {
                val hostBounds = scope.hostBounds.takeUnless { it.isEmpty } ?: elementBounds
                val gradientLine = calculateGradientLine(
                    hostBounds = hostBounds,
                    elementBounds = elementBounds,
                    progress = scope.progress.value,
                    highlightWidthPx = scope.style.highlightWidth.toPx(),
                    tiltDegrees = scope.style.tiltDegrees,
                    direction = scope.style.direction,
                )
                val brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to scope.style.baseColor,
                        0.5f to scope.style.highlightColor,
                        1f to scope.style.baseColor,
                    ),
                    start = gradientLine.start,
                    end = gradientLine.end,
                )
                drawPlaceholder(brush = brush, shape = shape ?: scope.style.shape)
            }
        }
    }

    private fun ContentDrawScope.drawPlaceholder(color: Color, shape: Shape) {
        if (size.isEmpty()) return
        val outline = shape.createOutline(size, layoutDirection, this)
        drawOutline(outline = outline, color = color)
    }

    private fun ContentDrawScope.drawPlaceholder(brush: Brush, shape: Shape) {
        if (size.isEmpty()) return
        val outline = shape.createOutline(size, layoutDirection, this)
        drawOutline(outline = outline, brush = brush)
    }
}
