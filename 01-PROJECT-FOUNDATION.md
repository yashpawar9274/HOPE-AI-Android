# PROMPT 01 — HOPE AI V2.0 Project Foundation

## What this phase builds

- A fresh native Android project that compiles.
- HOPE AI V2.0 branding and launcher identity.
- Kotlin, Jetpack Compose, Material 3, Hilt and Navigation foundation.
- Premium dark design system and initial app shell.
- Safe configuration placeholders for later Supabase and Firebase phases.

## Copy this prompt into your coding agent

You are working on Phase 01 of HOPE AI V2.0. Create a fresh native Android
application and complete this phase before starting authentication, AI, voice,
Firebase, Supabase or Mem0 functionality.

Product identity:

- App name: HOPE AI
- Version name: 2.0.0
- Version code: 20
- Application ID: com.yashpawar.hopeai
- Creator: Yash Pawar
- Minimum supported version: Android 8 or newer
- Default theme: dark
- Languages prepared for: English, Hindi and Hinglish
- No emoji in app copy, buttons, reactions or notifications

Engineering requirements:

1. Use Kotlin and Gradle Kotlin DSL.
2. Use a Gradle version catalog.
3. Use current stable, mutually compatible Android Gradle Plugin, Kotlin,
   Compose BOM, Material 3, Navigation Compose, Lifecycle and Hilt versions.
4. Use one `app` module for Phase 01 with clean package boundaries that can be
   extracted into modules later.
5. Configure Java/Kotlin toolchain compatibility required by the selected
   Android Gradle Plugin.
6. Enable Compose, BuildConfig and resource shrinking configuration, but do not
   enable release shrinking until the release phase.
7. Create debug and release build types. Release signing must use an untracked
   properties file or environment variables; never add credentials.
8. Add `.gitignore` entries for local properties, keystores, service account
   JSON, Supabase secrets, Gemini keys, Mem0 keys and generated build output.

Create this package structure:

```text
com.yashpawar.hopeai
├── HopeApplication.kt
├── MainActivity.kt
├── app/navigation
├── core/common
├── core/network
├── core/security
├── core/ui/components
├── core/ui/theme
├── data
├── domain/model
├── domain/repository
├── domain/usecase
├── feature/splash
├── feature/home
├── feature/about
└── service
```

Implement:

- `HopeApplication` with Hilt.
- `MainActivity` using edge-to-edge Compose.
- `HopeApp` as the root composable.
- A typed navigation graph with Splash, Home and About routes.
- Splash screen that displays the provided HOPE logo and the exact app name
  “HOPE AI”.
- Home screen with the HOPE avatar/logo, greeting, status “Ready”, and disabled
  preview cards for Voice, Chat, Memory, Tasks and Integrations. Clearly label
  them “Coming in the next build”; do not simulate working features.
- About screen showing “HOPE AI V2.0” and “Created by Yash Pawar”.

Design system:

- Midnight navy background.
- Electric blue and violet accent lighting.
- Soft white primary text.
- Accessible secondary text.
- Rounded surfaces with restrained glass depth.
- Minimum 48dp touch targets.
- Dynamic type support.
- Reduced-motion setting scaffold.
- High-contrast-compatible colors.
- No emoji icons; use Material icons or original vector assets.

Logo handling:

- Use `assets/hope-ai-v2-logo.png` as the source artwork.
- Create appropriate Android launcher/adaptive icon resources without cropping
  the central H/orb mark.
- Use the mark on splash and About screens.
- Keep the app name as Android-rendered text; do not rasterize the words into
  the icon.
- Add content descriptions where the image is informative; mark decorative
  occurrences appropriately.

Create reusable Compose components:

- `HopeScaffold`
- `HopeTopBar`
- `HopeCard`
- `HopePrimaryButton`
- `HopeSecondaryButton`
- `HopeLoadingIndicator`
- `HopeEmptyState`
- `HopeErrorState`
- `HopeAvatarFrame`

State and architecture:

- Use immutable UI state models.
- Use ViewModels only where state exists.
- Do not add repositories that return fake successful AI/backend data.
- Add interfaces for future network/session configuration without hardcoded
  secrets.
- UI previews may use clearly named preview-only data.

Configuration preparation:

- Add documented placeholders for public Supabase URL, public Supabase anon
  key and Firebase Android configuration.
- Do not add `google-services.json` yet unless the user supplies the Android
  client file for package `com.yashpawar.hopeai`.
- Do not add Gemini, Mem0, Supabase service-role or Firebase Admin secrets.

Tests:

- Unit test initial route selection.
- Compose test that Splash reaches Home.
- Compose test that About shows HOPE AI V2.0 and Yash Pawar.
- Test that primary buttons meet semantic labels.

Verification commands:

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

Phase 01 is complete only when all commands pass and the generated APK installs
and opens on an emulator or real Android device.

At the end, report:

1. every file created or modified;
2. dependency versions selected;
3. commands run and their exit status;
4. APK output path;
5. known limitations;
6. configuration still required;
7. confirmation that Phase 02 has not been started.

Do not proceed to Phase 02 automatically.

## Check before moving on

- [ ] App installs and opens.
- [ ] Package is `com.yashpawar.hopeai`.
- [ ] Version is `2.0.0` / code `20`.
- [ ] HOPE logo is not cropped by round or squircle masks.
- [ ] Splash, Home and About work.
- [ ] No secret is committed.
- [ ] No fake AI or integration success is shown.
- [ ] Tests, lint and debug build pass.

