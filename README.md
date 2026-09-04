# Compose Skeleton

A lightweight Jetpack Compose library for layout-aware skeleton placeholders with a synchronized shimmer effect.

The skeleton uses the size already measured by Compose. You keep building the real component and add `Modifier.skeleton()` where you would add a background. One `SkeletonHost` coordinates the loading state and shimmer across all participating children.

## Status

The project is under active development and is not published to a package repository yet. The `example` module demonstrates the current API.

## Modules

| Module | Purpose |
| --- | --- |
| `skeleton` | Reusable Compose library |
| `example` | Interactive Android example app |

## Usage

```kotlin
SkeletonHost(enabled = loading) {
    OutlinedCard(
        modifier = Modifier.skeleton(SkeletonMode.Keep),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .skeleton(SkeletonMode.Shimmer),
        )

        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .skeleton(SkeletonMode.Solid),
        )
    }
}
```

`Modifier.skeleton()` does not change measurement. Use `width`, `height`, `size`, `fillMaxWidth`, and other layout modifiers to define the placeholder dimensions. Outside a `SkeletonHost`, the modifier is a no-op.

## Modes

| Mode | Loading behavior |
| --- | --- |
| `Shimmer` | Replaces content with the shared animated gradient |
| `Solid` | Replaces content with the base placeholder color |
| `Keep` | Draws the real content, useful for card shells, borders, and status labels |
| `Hide` | Draws nothing while preserving layout space |

A multiline `Text` is represented by one rectangle. Use separate `Text` composables when each line should have its own width or mode.

## Styling

```kotlin
val style = SkeletonDefaults.style(
    baseColor = Color(0xFFE2E5E9),
    highlightColor = Color(0xFFF7F8FA),
    shape = RoundedCornerShape(10.dp),
    animationDurationMillis = 1_000,
    highlightWidth = 140.dp,
    tiltDegrees = 18f,
    direction = SkeletonDirection.LeftToRight,
)

SkeletonHost(
    enabled = loading,
    animationEnabled = animationsAllowed,
    style = style,
) {
    Content()
}
```

The default colors adapt to the system light or dark theme. Setting `animationEnabled` to `false` renders shimmer elements as solid placeholders. Skeleton content is removed from accessibility semantics while loading, except for elements using `Keep`.

`animationDurationMillis` is the time for one complete shimmer sweep. Lower values move faster. Updating a duration while loading restarts the sweep; updating colors keeps its current position.

Use `baseColor` for every solid placeholder and the outside of the shimmer, and `highlightColor` for the bright band. Pass `Color.Unspecified` to `SkeletonDefaults.style()` to retain its theme-aware color. Individual elements can override both colors while sharing the host animation:

```kotlin
Box(
    modifier = Modifier
        .size(72.dp)
        .skeleton(
            colors = SkeletonColors(
                baseColor = Color(0xFF263238),
                highlightColor = Color(0xFF80CBC4),
            ),
        ),
)
```

## Build

```shell
./gradlew :skeleton:testDebugUnitTest :skeleton:lintDebug
./gradlew :example:assembleDebug :example:lintDebug
```

The project requires JDK 21 and Android SDK platform 37.

## License

Compose Skeleton is available under the Apache License 2.0. See [LICENSE](LICENSE).
