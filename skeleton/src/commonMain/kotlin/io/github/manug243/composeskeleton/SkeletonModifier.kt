package io.github.manug243.composeskeleton

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import io.github.manug243.composeskeleton.internal.SkeletonElement

/**
 * Marks this element as a skeleton placeholder inside a [SkeletonHost].
 *
 * The placeholder uses the size Compose already measured for the element. Outside a
 * [SkeletonHost], or while the host is disabled, the element is drawn and behaves normally.
 *
 * While the host is enabled and [mode] is not [SkeletonMode.Keep]:
 * - The placeholder replaces the content drawn by modifiers after `.skeleton()` and by the
 *   element's children. Modifiers before `.skeleton()`, such as a `background`, are still drawn.
 * - The semantics of the element's children and of modifiers after `.skeleton()` are cleared,
 *   and the element is hidden from accessibility services.
 * - Presses on the element and its children are blocked, so `clickable`, `toggleable`,
 *   `selectable` and tap gesture detectors do not fire, whether they come before or after
 *   `.skeleton()` in the modifier chain. Drags are not blocked, so an enclosing scroll container
 *   can still be scrolled from a placeholder.
 * - Focus targets after `.skeleton()` and inside the element cannot gain keyboard focus, and
 *   focus that is already inside the element is cleared.
 *
 * [SkeletonMode.Keep] elements stay fully interactive, but a press that lands on one of their
 * placeholder children is blocked like any other press on a placeholder.
 *
 * Known limitations, because a modifier cannot affect modifiers before it in the chain:
 * - Place `.skeleton()` **before** `clickable`, `focusable` and similar modifiers. A focusable
 *   modifier before `.skeleton()` can still gain keyboard focus, and the click action of a
 *   `clickable` before `.skeleton()` stays in the semantics tree (the node is hidden from
 *   accessibility services, but semantics actions can still be invoked directly).
 * - Mouse hover enter and exit events are not blocked, so hover effects of the element and its
 *   children still show while loading.
 * - A touch that lands just outside a small element, within its minimum touch target, is not
 *   blocked. Give small clickable placeholders a layout size of at least the minimum touch
 *   target.
 *
 * @param mode How the element behaves while the host is enabled.
 * @param shape Overrides [SkeletonStyle.shape] for this element, or `null` to use the host's.
 * @param colors Overrides the host colors for this element, or `null` to use the host's.
 */
public fun Modifier.skeleton(
    mode: SkeletonMode = SkeletonMode.Shimmer,
    shape: Shape? = null,
    colors: SkeletonColors? = null,
): Modifier = this.then(SkeletonElement(mode = mode, shape = shape, colors = colors))
