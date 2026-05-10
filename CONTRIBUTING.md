# Contributing

## App Setup

1. Clone the repo and checkout the feature branch:

```powershell
git checkout feature/artist_backend_wiring
```

2. Create local app config:

```powershell
.\scripts\setup-dev.ps1
```

This copies `local.properties.example` to `local.properties` if it does not already exist.

3. Build the app:

```powershell
.\gradlew.bat :app:assembleDebug
```

4. Install on a connected Android device:

```powershell
.\gradlew.bat :app:installDebug
```

## Supabase Access

The app can run with the checked-in publishable key from `local.properties.example`.

For schema changes, seed data, or database debugging, you need to be invited to the Supabase project. After accepting the invite:

```powershell
npx.cmd supabase login
npx.cmd supabase link --project-ref nwzcylzzudpwghqfhdks
```

Apply the current schema:

```powershell
npx.cmd supabase db query --linked --file supabase/artist_onboarding_v1.sql
```

Apply demo artist seed data:

```powershell
npx.cmd supabase db query --linked --file fake_artists_db.sql
```

## Current Prototype Notes

- OTP and live selfie UI are visible but disabled for now.
- Fresh artist onboarding uses a temporary local email session.
- Supabase RLS is temporarily relaxed for prototype onboarding writes.
- Tighten auth/RLS before production.
