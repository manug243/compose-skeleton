package io.github.manug243.composeskeleton

/** How a [skeleton] element is drawn while its [SkeletonHost] is enabled. */
public enum class SkeletonMode {
    /** Draws an animated shimmer placeholder (static if animation is disabled). */
    Shimmer,

    /** Draws a static base-color placeholder. */
    Solid,

    /** Draws the real content and keeps its semantics, e.g. a card shell around placeholders. */
    Keep,

    /** Draws nothing but still occupies its layout space. */
    Hide,
}
