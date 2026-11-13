package io.github.manug243.composeskeleton

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * App-wide [SkeletonStyle] used by every [SkeletonHost] that is not given a `style` explicitly.
 *
 * Provide it once in your app theme, for example to match the placeholder colors to your own
 * light and dark color schemes:
 *
 * ```
 * CompositionLocalProvider(
 *     LocalSkeletonStyle provides SkeletonDefaults.style(darkTheme = isAppInDarkMode),
 * ) {
 *     content()
 * }
 * ```
 *
 * The default value is `null`, in which case hosts fall back to [SkeletonDefaults.style].
 */
public val LocalSkeletonStyle: ProvidableCompositionLocal<SkeletonStyle?> =
    staticCompositionLocalOf { null }
