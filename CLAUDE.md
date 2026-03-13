# CamperChecks — Claude Instructions

## What This App Does
Android app for camper/motorhome owners to **track** rentals from external providers (default: Yescapa). It does **not** handle booking — it manages checklists, condition reports, income tracking, and renter contacts around rentals that already exist externally.

## Tech Stack
- **Language:** Kotlin 2.3.10
- **UI:** Jetpack Compose + Material3 (BOM 2026.03.00) + Navigation3
- **DI:** Koin 4.1.1
- **Local DB:** Room 2.8.4
- **Backend:** Firebase (Auth, Firestore, Crashlytics, AI, AppCheck)
- **On-device AI:** Llamatik
- **Build:** AGP 9.1.0, compileSdk/targetSdk 36, minSdk 35, Java 21
- **Code style:** ktlint

## Project Structure
```
app/                  # Main application module
icons/                # Shared icon resources module
build-logic/          # Convention plugins (version management via version.properties)
certs/                # Signing keystores (debug + fakeRelease committed; real release via env)
gradle/libs.versions.toml  # Version catalog — all deps go here
```

## Module Conventions
- All dependencies must be declared in `gradle/libs.versions.toml`
- New feature modules should be added to `settings.gradle.kts` and included via `projects.*` accessors
- The version is managed in `version.properties` (major.minor.patch.snapshot) — never edit `versionCode`/`versionName` manually in build files

## Code Conventions
- Follow ktlint rules (enforced via the `ktlint` Gradle plugin)
- Use `@Serializable` for navigation route data classes
- Opt-ins already configured globally: `ExperimentalMaterial3Api`, `ExperimentalMaterial3ExpressiveApi`, `ExperimentalCoroutinesApi`, `ExperimentalTime`
- Package root: `com.alorma.camperchecks`

## Architecture
- MVVM with ViewModels (Koin inject) + Compose UI
- Firestore data scoped to `/users/{uid}/...` — never store data outside the user's subtree
- Room for local/offline data; Firestore for cloud sync
- Single-activity, navigation via Navigation3

## Firebase / Security
- App Check is enabled (Play Integrity in release, debug token via `local.properties` or env `DEBUG_APP_CHECK_TOKEN`)
- Auth: Google Sign-In only — app must block all data access when signed out
- Security rules must enforce per-user isolation (issue #11)

## GitHub Issues
Tracked at: https://github.com/alorma/camper-rent-assistant/issues
Issues use labels: `type:`, `priority:`, `area:` and milestones (`v1 (MVP)`).

## Do Not
- Do not add Firebase Auth providers other than Google Sign-In (v1 scope)
- Do not store user data outside `/users/{uid}/` in Firestore
- Do not skip AppCheck setup in new Firebase calls
- Do not commit `google-services.json` or real keystore files
- Do not modify `versionCode`/`versionName` directly — use `version.properties`
