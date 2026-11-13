# Contributing to Compose Skeleton

Thanks for considering a contribution. Compose Skeleton is maintained by one person in spare time, so replies can take a few days.

## Before you start

- For bug reports, search existing issues first, then open one using the bug report form.
- For new features or API changes, open an issue first and describe the problem and the proposed API. This avoids spending time on a change that does not fit the library's scope.
- For questions about usage, use [GitHub Discussions](https://github.com/manug243/compose-skeleton/discussions) instead of issues.

## Build and test

Requirements:

- JDK 21
- Android SDK platform 37
- macOS, only if you want to build or test the iOS targets

Commands:

```shell
./gradlew :skeleton:desktopTest :skeleton:testAndroidHostTest :skeleton:lint
./gradlew :skeleton:jsTest :skeleton:wasmJsTest
./gradlew :skeleton:checkKotlinAbi
./gradlew :example:assembleDebug :example:lintDebug
```

The web tests need a local Chrome. If you change the public API on purpose, run `./gradlew :skeleton:updateKotlinAbi` and commit the updated files in `skeleton/api`.

The iOS targets can only be built on macOS:

```shell
./gradlew :skeleton:iosSimulatorArm64Test
```

## Pull requests

- Target the `develop` branch. Never open pull requests against `main`. `main` only receives merges from `develop` and release tags.
- Keep each pull request focused on one change.
- Use [conventional commit](https://www.conventionalcommits.org/) messages, for example `feat: add darkTheme parameter` or `fix: restart sweep on duration change`.
- Public API changes must include KDoc and tests.
- Add or update tests for any behaviour change.
- Add an entry under `Unreleased` in [CHANGELOG.md](CHANGELOG.md) for user-visible changes.
- Make sure the build and test commands above pass before requesting a review.

The pull request template contains a checklist that covers these points.

## Code style

- Follow the existing Kotlin style in the repository.
- The library code lives in `commonMain` and uses only common Compose APIs. Do not add platform-specific code without discussing it in an issue first.

## Conduct and security

By participating, you agree to abide by the [Code of Conduct](CODE_OF_CONDUCT.md). Do not report security vulnerabilities in public issues; see [SECURITY.md](SECURITY.md).

## License

By contributing, you agree that your contributions are licensed under the Apache License 2.0, the same license as the project.
