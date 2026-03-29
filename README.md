# Artist Onboard

Artist Onboard is an Android app built with Kotlin and Jetpack Compose for onboarding two kinds of users:

- Artists who want to create a profile, define their offerings, pricing, delivery, live-art availability, and upload portfolio images
- Customers who want to describe their occasion, style, budget, and timeline before moving into a matching flow

The project currently focuses on the onboarding experience, visual flow, and local data storage for artist submissions.

## Highlights

- Role selection entry screen for artist and customer journeys
- Multi-step artist onboarding with category, style, pricing, materials, customization, capacity, delivery, portfolio, and story screens
- Customer onboarding flow covering occasion, budget, style, and timeline
- Jetpack Compose UI with Navigation Compose
- Local persistence using Room
- Asset-rich visual design for the onboarding experience

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room
- Coil
- Gradle Kotlin DSL

## Project Info

- App name: `Artist Onboard`
- Package: `com.titu.artistonboard`
- Min SDK: `24`
- Target SDK: `34`
- Compile SDK: `34`
- Version: `1.0`

## Project Structure

```text
app/src/main/java/com/titu/artistonboard/
|- MainActivity.kt
|- RoleSelectionScreen.kt
|- OnboardingScreens.kt
|- UserOnboardingScreens.kt
|- data/
|  |- Artist.kt
|  |- ArtistDao.kt
|  |- ArtistDatabase.kt
|  |- ArtistRepository.kt
|  |- Converters.kt
|- ui/
   |- components/
   |- theme/
```

## Running The App

1. Open the project in Android Studio.
2. Let Gradle sync complete.
3. Use an emulator or connected Android device.
4. Run the `app` configuration.

## Data Notes

- The app uses a local Room database named `artist_onboard.db`.
- The database is configured with `fallbackToDestructiveMigration()`, so schema changes can reset local data during development.

## Repository Notes

- Build output, IDE files, local machine config, and heap dump files are excluded through `.gitignore`.
- Large design/image assets used by the app are included in the repository.

## Status

This repository is an app prototype focused on onboarding UX and flow implementation. It is a strong base for adding backend sync, authentication, analytics, and real artist-customer matching in future iterations.
