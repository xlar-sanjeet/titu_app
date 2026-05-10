# Artist Onboard

Artist Onboard is an Android app for onboarding artists onto a handmade/custom art marketplace. Artists can create a profile, describe what they make, configure live art services, pricing, customization, capacity, materials, delivery, portfolio, and story details, then reach a dashboard.

The current branch wires the artist onboarding flow to Supabase for backend persistence.

## Current Branch

```text
feature/artist_backend_wiring
```

## What Works Now

- Artist onboarding flow saves profile data to Supabase.
- Returning completed artists route to the dashboard.
- Dashboard shows basic profile/live-service status.
- Dashboard has a Logout option to clear the temporary local session.
- OTP and live selfie UI are visible but disabled for now.
- Two seeded demo artists exist for dashboard testing:
  - `picasso@titu.com` without live services
  - `vangogh@titu.com` with live services

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Supabase Kotlin SDK
- Supabase PostgREST, Auth, and Storage modules
- Gradle Kotlin DSL

## Quick Start

Clone the repo, checkout the feature branch, and run the setup script:

```powershell
git checkout feature/artist_backend_wiring
.\scripts\setup-dev.ps1
```

Build the app:

```powershell
.\gradlew.bat :app:assembleDebug
```

Install on a connected Android device:

```powershell
.\gradlew.bat :app:installDebug
```

Check connected devices:

```powershell
adb devices -l
```

## Local Configuration

The app reads Supabase values from `local.properties`.

`local.properties` is intentionally gitignored. Contributors should create it by running:

```powershell
.\scripts\setup-dev.ps1
```

That copies:

```text
local.properties.example -> local.properties
```

The example file contains the project URL and publishable key used by the app.

## Supabase Setup

The app can run with the publishable key. For database schema changes, seed updates, or debugging, contributors must be invited to the Supabase project.

After accepting the Supabase invite:

```powershell
npx.cmd supabase login
npx.cmd supabase link --project-ref nwzcylzzudpwghqfhdks
```

Apply schema:

```powershell
npx.cmd supabase db query --linked --file supabase/artist_onboarding_v1.sql
```

Apply demo seed data:

```powershell
npx.cmd supabase db query --linked --file fake_artists_db.sql
```

## Prototype Auth Behavior

Real OTP and live selfie verification are intentionally disabled for now.

For fresh onboarding:

1. Enter name, phone, and email.
2. Continue through onboarding.
3. Complete the final story step.
4. The app marks the artist profile as `completed`.
5. On the next launch with the same local session, the app opens the dashboard.

Important: this branch uses a temporary local email session so the team can test onboarding without auth friction. Supabase RLS is also temporarily relaxed for prototype onboarding writes. Tighten auth and RLS before production.

## Project Structure

```text
app/src/main/java/com/titu/artistonboard/
|- MainActivity.kt
|- RoleSelectionScreen.kt
|- OnboardingScreens.kt
|- supabase/
|  |- LocalArtistSession.kt
|  |- SupabaseArtistOnboardingRepository.kt
|  |- SupabaseArtistOnboardingViewModel.kt
|  |- SupabaseAuthRepository.kt
|  |- SupabaseProvider.kt
|  |- SupabaseStorageRepository.kt
|- ui/dashboard/
|  |- ArtistDashboardRepository.kt
|  |- ArtistDashboardScreen.kt
|  |- ArtistDashboardViewModel.kt
|- ui/theme/

supabase/
|- artist_onboarding_v1.sql
|- SETUP.md
|- config.toml

scripts/
|- setup-dev.ps1
```

## Useful Commands

Build:

```powershell
.\gradlew.bat :app:assembleDebug
```

Install:

```powershell
.\gradlew.bat :app:installDebug
```

View device logs:

```powershell
adb logcat
```

Clear app data:

```powershell
adb shell pm clear com.titu.artistonboard
```

Check git branch:

```powershell
git branch --show-current
```

## Notes For Contributors

- Do not commit `local.properties`.
- Do not commit Supabase personal access tokens or service-role keys.
- Use `local.properties.example` for public app config only.
- If you change database schema, update `supabase/artist_onboarding_v1.sql`.
- If you change setup steps, update both `README.md` and `CONTRIBUTING.md`.
