package io.github.manug243.composeskeleton

import kotlin.jvm.JvmInline

/**
 * How a [skeleton] element is drawn and behaves while its [SkeletonHost] is enabled.
 *
 * This is a value class with a fixed set of constants rather than an enum, so new modes can be
 * added in future versions without breaking binary compatibility. When matching on a mode with
 * `when`, always include an `else` branch.
 */
@JvmInline
public value class SkeletonMode private constructor(private val value: Int) {

    override fun toString(): String = when (this) {
        Shimmer -> "Shimmer"
        Solid -> "Solid"
        Keep -> "Keep"
        Hide -> "Hide"
        else -> "Invalid"
    }

    /** The available skeleton modes. */
    public companion object {
        /**
         * Draws an animated shimmer placeholder (static if animation is disabled). The element
         * is hidden from accessibility services and does not receive input.
         */
        public val Shimmer: SkeletonMode = SkeletonMode(0)

        /**
         * Draws a static base-color placeholder. The element is hidden from accessibility
         * services and does not receive input.
         */
        public val Solid: SkeletonMode = SkeletonMode(1)

        /**
         * Draws the real content and keeps its semantics and input handling, e.g. a card shell
         * around placeholders.
         */
        public val Keep: SkeletonMode = SkeletonMode(2)

        /**
         * Draws nothing but still occupies its layout space. The element is hidden from
         * accessibility services and does not receive input.
         */
        public val Hide: SkeletonMode = SkeletonMode(3)
    }
}
