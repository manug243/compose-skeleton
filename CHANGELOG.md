# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
While the version is `0.x`, minor releases may contain breaking changes.

## [Unreleased]

Planned for 1.0.0-rc1.

### Added

- Web targets: `wasmJs` and `js`.
- `darkTheme` parameter on `SkeletonDefaults.style()`.
- `LocalSkeletonStyle` composition local.
- Placeholders block presses and keyboard focus while loading; `Keep` elements stay interactive.
- `copy()` on `SkeletonStyle` and `SkeletonColors`.
- ABI validation of the public API (`skeleton/api`).

### Changed (breaking)

- `SkeletonStyle` and `SkeletonColors` are no longer data classes.
- `SkeletonMode` is a value class with constants instead of an enum. `SkeletonMode.Shimmer` and the other constants are used as before; a `when` over the mode needs an `else` branch.

## [0.1.0] - 2026-11-08

First public release.

### Added

- `SkeletonHost` to coordinate the loading state and a shared shimmer across all child skeletons.
- `Modifier.skeleton()` to mark placeholder elements. Outside a `SkeletonHost` the modifier is a no-op.
- Modes: `Shimmer`, `Solid`, `Keep` and `Hide`.
- Styling through `SkeletonDefaults.style()` and per-element `SkeletonColors`, with theme-aware default colors.
- Targets: Android (`minSdk 24`), Desktop (JVM) and iOS (`iosArm64`, `iosSimulatorArm64`).
- Publishing to Maven Central from Git tags.

[Unreleased]: https://github.com/manug243/compose-skeleton/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/manug243/compose-skeleton/releases/tag/v0.1.0
