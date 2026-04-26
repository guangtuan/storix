# Repository Guidelines

## Project Structure & Module Organization
`Storix` is a single-module Android app (`:app`) built with Kotlin + Jetpack Compose.

- App code: `app/src/main/java/com/storix/app`
- UI layer: `app/src/main/java/com/storix/app/ui` and `ui/theme`
- Data layer: `app/src/main/java/com/storix/app/data` (`local`, `remote`, `repository`, `backup`)
- Android resources: `app/src/main/res` (`values`, `drawable`, launcher assets)
- CI workflow: `.github/workflows/android.yml`
- Release helper: `dev_deploy.sh`

## Build, Test, and Development Commands
- `./gradlew assembleDebug`: Build a local debug APK.
- `./gradlew build`: Full CI-style build (used in GitHub Actions).
- `./gradlew lint`: Run Android lint checks.
- `./gradlew testDebugUnitTest`: Run JVM unit tests.
- `./gradlew connectedDebugAndroidTest`: Run instrumentation tests on a connected device/emulator.
- `./dev_deploy.sh --dry-run --dest user@host:/path/`: Preview APK upload command.

## Coding Style & Naming Conventions
- Follow Kotlin defaults: 4-space indentation, no tabs.
- Class/file names use `PascalCase` (for example, `AssetRepository.kt`, `MainViewModel.kt`).
- Functions/properties use `camelCase`; constants use `UPPER_SNAKE_CASE`.
- Compose screens/components use `PascalCase` function names (for example, `StorixApp`).
- Packages stay lowercase under `com.storix.app.*`.
- Resource IDs use `snake_case`; keep user-facing text in `res/values/strings.xml`.

## Testing Guidelines
No committed test sources are present yet; add tests with new features and bug fixes.

- Unit tests: `app/src/test/java/...`
- Instrumentation/UI tests: `app/src/androidTest/java/...`
- Test class naming: `<ClassName>Test` (for example, `AssetRepositoryTest`)
- Prefer behavior-based test names such as `savesAsset_whenFieldsAreValid`.
- Before opening a PR, run `./gradlew build lint testDebugUnitTest`.

## Commit & Pull Request Guidelines
Recent commits favor short, imperative subjects (for example, `Add asset import-export backup flow`) with occasional `feat:`/`refactor:` prefixes.

- Keep commit titles concise and action-oriented; one logical change per commit.
- PRs should include: purpose, key changes, validation steps, and linked issue(s).
- For UI changes, attach screenshots or a short screen recording.
- Ensure CI passes and note any follow-up work explicitly.

## Security & Configuration Tips
- Do not commit signing secrets. Use `keystore.properties.example` as a template.
- Release tasks require a valid local `keystore.properties`; Gradle enforces this for release builds.
