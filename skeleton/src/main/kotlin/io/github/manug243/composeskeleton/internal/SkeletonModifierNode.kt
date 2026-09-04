package io.github.manug243.composeskeleton.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
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
import io.github.manug243.composeskeleton.SkeletonColors
import io.github.manug243.composeskeleton.SkeletonDirection
import io.github.manug243.composeskeleton.SkeletonMode

internal data class SkeletonElement(
    val mode: SkeletonMode,
    val shape: Shape?,
    val colors: SkeletonColors?,
) : ModifierNodeElement<SkeletonNode>() {
    override fun create(): SkeletonNode = SkeletonNode(mode = mode, shape = shape, colors = colors)

    override fun update(node: SkeletonNode) {
        node.mode = mode
        node.shape = shape
        node.colors = colors
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "skeleton"
        properties["mode"] = mode
        properties["shape"] = shape
        properties["colors"] = colors
    }
}

internal class SkeletonNode(
    var mode: SkeletonMode,
    var shape: Shape?,
    var colors: SkeletonColors?,
) : Modifier.Node(),
    DrawModifierNode,
    GlobalPositionAwareModifierNode,
    CompositionLocalConsumerModifierNode,
    SemanticsModifierNode {

    private var elementBounds: Rect = Rect.Zero
    private var cachedGeometry: ShimmerGeometry? = null
    private var cachedGeometryHostBounds: Rect = Rect.Zero
    private var cachedGeometryWidth = Float.NaN
    private var cachedGeometryTilt = Float.NaN
    private var cachedGeometryDirection: SkeletonDirection? = null
    private var cachedColorStopsColors: SkeletonColors? = null
    private var cachedColorStops: Array<Pair<Float, Color>> = emptyArray()
    private var cachedOutline: Outline? = null
    private var cachedOutlineShape: Shape? = null
    private var cachedOutlineSize = Size.Zero
    private var cachedOutlineLayoutDirection: androidx.compose.ui.unit.LayoutDirection? = null
    private var cachedOutlineDensity = Float.NaN
    private var cachedOutlineFontScale = Float.NaN
    private var cachedHostColorsStyle: io.github.manug243.composeskeleton.SkeletonStyle? = null
    private var cachedHostColors: SkeletonColors? = null

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val newBounds = coordinates.boundsInWindow(clipBounds = false)
        if (elementBounds != newBounds) {
            elementBounds = newBounds
            invalidateDraw()
        }
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
                drawPlaceholder(color = resolvedColors(scope).baseColor, shape = shape ?: scope.style.shape)
            }
            else -> {
                val hostBounds = scope.hostBounds.takeUnless { it.isEmpty } ?: elementBounds
                val geometry = geometryFor(hostBounds, scope)
                val gradientLine = geometry.gradientLine(elementBounds, scope.progress.value)
                val colors = resolvedColors(scope)
                val brush = Brush.linearGradient(
                    colorStops = colorStopsFor(colors),
                    start = gradientLine.start,
                    end = gradientLine.end,
                )
                drawPlaceholder(brush = brush, shape = shape ?: scope.style.shape)
            }
        }
    }

    private fun ContentDrawScope.drawPlaceholder(color: Color, shape: Shape) {
        if (size.isEmpty()) return
        val outline = outlineFor(shape)
        drawOutline(outline = outline, color = color)
    }

    private fun ContentDrawScope.drawPlaceholder(brush: Brush, shape: Shape) {
        if (size.isEmpty()) return
        val outline = outlineFor(shape)
        drawOutline(outline = outline, brush = brush)
    }

    private fun resolvedColors(scope: SkeletonScopeState): SkeletonColors {
        colors?.let { return it }
        if (cachedHostColorsStyle != scope.style) {
            cachedHostColorsStyle = scope.style
            cachedHostColors = SkeletonColors(scope.style.baseColor, scope.style.highlightColor)
        }
        return requireNotNull(cachedHostColors)
    }

    private fun ContentDrawScope.geometryFor(hostBounds: Rect, scope: SkeletonScopeState): ShimmerGeometry {
        val width = scope.style.highlightWidth.toPx()
        if (
            cachedGeometry == null ||
            cachedGeometryHostBounds != hostBounds ||
            cachedGeometryWidth != width ||
            cachedGeometryTilt != scope.style.tiltDegrees ||
            cachedGeometryDirection != scope.style.direction
        ) {
            cachedGeometry = ShimmerGeometry(
                hostBounds = hostBounds,
                highlightWidthPx = width,
                tiltDegrees = scope.style.tiltDegrees,
                direction = scope.style.direction,
            )
            cachedGeometryHostBounds = hostBounds
            cachedGeometryWidth = width
            cachedGeometryTilt = scope.style.tiltDegrees
            cachedGeometryDirection = scope.style.direction
        }
        return requireNotNull(cachedGeometry)
    }

    private fun colorStopsFor(colors: SkeletonColors): Array<Pair<Float, Color>> {
        if (cachedColorStopsColors != colors) {
            cachedColorStopsColors = colors
            cachedColorStops = arrayOf(
                0f to colors.baseColor,
                0.5f to colors.highlightColor,
                1f to colors.baseColor,
            )
        }
        return cachedColorStops
    }

    private fun ContentDrawScope.outlineFor(shape: Shape): Outline {
        if (
            cachedOutline == null ||
            cachedOutlineShape != shape ||
            cachedOutlineSize != size ||
            cachedOutlineLayoutDirection != layoutDirection ||
            cachedOutlineDensity != density ||
            cachedOutlineFontScale != fontScale
        ) {
            cachedOutline = shape.createOutline(size, layoutDirection, this)
            cachedOutlineShape = shape
            cachedOutlineSize = size
            cachedOutlineLayoutDirection = layoutDirection
            cachedOutlineDensity = density
            cachedOutlineFontScale = fontScale
        }
        return requireNotNull(cachedOutline)
    }
}
