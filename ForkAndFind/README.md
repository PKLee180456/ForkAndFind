# Fork & Find

A native Kotlin Android app built with Jetpack Compose and Material 3. Cream backgrounds, forest-green accents, illustrated food cards, and a scrollable interface that works on small screens.

## Open in Android Studio

1. Install Android Studio Meerkat (2024.3.1) or a newer compatible release.
2. Choose **Open** and select this `ForkAndFind` folder (the folder containing `settings.gradle.kts`).
3. Use the bundled JDK 17 or newer compatible JDK as the Gradle JDK under Settings → Build, Execution, Deployment → Build Tools → Gradle.
4. Install **Android SDK Platform 35** through SDK Manager if prompted. Allow Gradle sync to download dependencies; first setup requires internet access.
5. Create an emulator running Android 8.0/API 26 or later, or connect a device with USB debugging enabled.
6. Select the **app** run configuration and press **Run**.

Android Studio creates `local.properties` with your SDK location. Do not share that machine-specific file.

## Features

- Instant case-insensitive search across names, cuisines, neighborhoods, and specialties. Multiple search words are combined.
- Cuisine and maximum-budget filters; vegetarian option switch.
- Sort by sample rating, price, or name.
- Favorites persisted on the device with SharedPreferences, surviving app restarts.
- Detail screen with description, specialty, favorite control, and a real neighborhood Maps search.
- Saved tab, empty results recovery, labeled controls, and system back navigation.
- Search and selection state survive activity recreation. All discovery features work offline.

## Data scope

The 12 Hong Kong listings are **fictional demo data**, including ratings, budget categories, and vegetarian availability. The interface labels them accordingly. No live availability, GPS distance, hours, reservations, real reviews, or restaurant API is claimed. Maps opens an external browser/app and searches the neighborhood, not a fictional business address. No API key, account, network permission, or location permission is required.

To add live data, replace `RestaurantCatalog` with a repository backed by your chosen restaurant provider; introduce asynchronous loading/error states and map the provider's fields to `Restaurant`. Keep provider secrets on your backend and follow its attribution and caching requirements.

## Build and test

Windows: `gradlew.bat assembleDebug testDebugUnitTest lintDebug`

macOS/Linux: `sh gradlew assembleDebug testDebugUnitTest lintDebug`

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

Unit tests cover multi-word search, intersecting filters, saved-only behavior, price ordering, and no results. A full Android build requires a local JDK and Android SDK; neither was available in the creation environment, so compilation, lint, and emulator verification must be run in Android Studio.

Build versions: Android Gradle Plugin 8.9.2, Gradle 8.11.1, Kotlin/Compose compiler 2.0.21, Compose BOM 2025.04.01, compile/target SDK 35, min SDK 26. Versions are pinned for reproducibility.
