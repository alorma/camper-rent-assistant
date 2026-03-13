# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This App Does
Android app for camper/motorhome owners to **track** rentals from external providers (default: Yescapa). It does **not** handle booking — it manages checklists, condition reports, income tracking, and renter contacts around rentals that already exist externally.

## Build Commands

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Build release APK (uses fakeRelease keystore when real one is absent)
./gradlew :app:assembleRelease

# Run unit tests
./gradlew :app:test

# Run a single test class
./gradlew :app:test --tests "com.alorma.camperchecks.SomeTest"

# Lint (ktlint check)
./gradlew ktlintCheck

# Auto-fix ktlint issues
./gradlew ktlintFormat

# Print current version info
./gradlew :app:version

# Print version name/code (for CI)
./gradlew :app:printVersionName
./gradlew :app:printVersionCode
```

## Tech Stack
- **Language:** Kotlin 2.3.10
- **UI:** Jetpack Compose + Material3 Expressive (BOM 2026.03.00) + Navigation3 1.0.1
- **DI:** Koin 4.1.1
- **Local DB:** Room 2.8.4 (schema exported to `app/schemas/`)
- **Backend:** Firebase (Auth, Firestore, Crashlytics, AI, AppCheck, RemoteConfig, Analytics)
- **On-device AI:** Llamatik 0.17.1
- **Build:** AGP 9.1.0, compileSdk/targetSdk 36, minSdk 35, Java 21
- **Code style:** ktlint 14.2.0

## Project Structure
```
app/                  # Main application module
  src/main/kotlin/com/alorma/camperchecks/
    AppApplication.kt     # Koin init (startKoin, workManagerFactory)
    MainActivity.kt       # Single activity, edge-to-edge, LocalSystemBarsAppearance
    App.kt                # Root @Composable (entry point for Navigation3 nav graph)
    di/                   # Koin modules (appModule includes all feature modules)
    ui/
      theme/              # AppTheme, AppThemeConfig, ThemePreferences, colors/, typography
      components/         # Shared UI: AppScaffold, StyledTopBars, StyledSettings*, feedback/
      loading/            # FullscreenLoading, WavyLoadingIndicator
      responsive/         # WindowSizeClass, ResponsiveSettingsContainer
      preview/            # @PreviewDynamicLightDark, PreviewTheme helpers
icons/                # Shared icon resources module (`:icons`)
build-logic/          # Convention plugins — only `camperchecks.version` plugin today
certs/                # Signing keystores (debug + fakeRelease committed; real release via env)
gradle/libs.versions.toml  # Version catalog — all deps go here
version.properties    # Canonical version source (major.minor.patch.snapshot)
```

## Module Conventions
- All dependencies declared in `gradle/libs.versions.toml`; never add inline versions in build files
- New feature modules: add to `settings.gradle.kts`, include via `projects.*` typesafe accessors
- Version managed in `version.properties` — the `camperchecks.version` plugin auto-sets `versionCode`/`versionName` from it; never edit them manually

## Architecture
- **MVVM**: ViewModels (Koin-injected via `koinViewModel()`) + Compose UI
- **Single activity**: `MainActivity` → `App()` composable → Navigation3 nav graph
- **DI**: `AppApplication` calls `startKoin { modules(appModule) }`; feature Koin modules are included into `appModule`
- **Room** for local/offline data; **Firestore** (`/users/{uid}/...`) for cloud sync — never store outside user's subtree
- **AppScaffold** wraps every screen: bundles `Scaffold` + `AppSnackbarHost` + `AppDialogHost` + `AppBottomSheetHost` via `CompositionLocal`s (`LocalAppSnackbarState`, `LocalAppDialogState`, `LocalAppBottomSheetState`)
- **AppTheme** (object): access `AppTheme.colorScheme`, `.typography`, `.shapes`, `.dims`, `.isDark` from any composable — backed by `MaterialExpressiveTheme` with `MotionScheme.expressive()`
- **ThemePreferences**: Koin singleton (`ThemePreferencesImpl`) holding `themeMode` and `useDynamicColors` state; injected into `AppTheme` composable

### BaseViewModel pattern
Every screen ViewModel extends `BaseViewModel<NavigationIntent, NavigationSideEffect, SideEffect>` (`ui/base/BaseViewModel.kt`):
- **NavigationIntent**: sealed class of user actions that trigger navigation (e.g. `DashboardNavigation.Settings`)
- **NavigationSideEffect**: one-shot navigation events emitted to the UI (e.g. `NavigateToSettings`)
- **SideEffect**: one-shot non-navigation UI events (snackbars, dialogs, bottom sheets)
- Override `navigate(NavigationIntent)` → call `emitNavigationSideEffect()`; call `emitSideEffect()` for UI feedback
- Collect both `navigationSideEffects` and `sideEffects` flows in the Screen composable

### Navigation3 screen pattern
- Each screen has a `*Route` object/data class annotated `@Serializable`, used as the `NavKey`
- Screens live under `screens/<feature>/` — `*Route.kt` + `*Screen.kt` (+ `*ViewModel.kt` when needed)
- Register new routes in the `entryProvider` block in `App.kt`; mutate `appBackStack` to navigate

## Code Conventions
- Follow ktlint rules — run `ktlintFormat` before committing
- Use `@Serializable` for Navigation3 route data classes
- Opt-ins configured globally (no need to annotate per-file): `ExperimentalMaterial3Api`, `ExperimentalMaterial3ExpressiveApi`, `ExperimentalCoroutinesApi`, `ExperimentalTime`, `ExperimentalMaterial3WindowSizeClassApi`
- Package root: `com.alorma.camperchecks`
- Logging: use **Timber** (not `Log`)
- Previews: use `@PreviewDynamicLightDark` + `PreviewTheme { }` wrappers (in `ui/theme/preview/`)

## Firebase / Security
- **App Check**: Play Integrity in release; debug token read from `local.properties` key `DEBUG_APP_CHECK_TOKEN` or env var `DEBUG_APP_CHECK_TOKEN` — exposed as `BuildConfig.DEBUG_APP_CHECK_TOKEN`
- **Auth**: Google Sign-In only — block all data access when signed out
- Security rules must enforce per-user isolation (`/users/{uid}/...`)
- Debug build suffix: `.dev` (`applicationId = com.alorma.camperchecks.dev`)

## Local Setup
1. Copy `google-services.json` (not committed) into `app/`
2. Optionally add `DEBUG_APP_CHECK_TOKEN=<token>` to `local.properties` for App Check in debug builds
3. Real release signing uses env vars `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` + `certs/release.keystore`; absent = falls back to committed `fakeRelease.keystore`

## GitHub Issues
Tracked at: https://github.com/alorma/camper-rent-assistant/issues
Labels: `type:`, `priority:`, `area:` — milestone: `v1 (MVP)`

## Do Not
- Do not add Firebase Auth providers other than Google Sign-In (v1 scope)
- Do not store user data outside `/users/{uid}/` in Firestore
- Do not skip AppCheck setup in new Firebase calls
- Do not commit `google-services.json` or real keystore files
- Do not modify `versionCode`/`versionName` directly — edit `version.properties`
- Do not use `MaterialTheme` directly in app code — use `AppTheme` object instead
