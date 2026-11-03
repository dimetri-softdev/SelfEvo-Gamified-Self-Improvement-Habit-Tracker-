# SelfEvo — Gamified Self-Improvement & Habit Tracker (OPSC63212 PoE)

[![Android CI](https://github.com/dimetri-softdev/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-/actions/workflows/build.yml/badge.svg)](https://github.com/dimetri-softdev/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-/actions)

## 👥 Team Members
- **Garren Gabron** (ST10460823)
- **Dimetri Peters** (ST10375943)
- **Nelson De Vos** (ST10451231)
- **Silindokuhle Gqukani** (ST10461013)

---

## 📱 Project Overview
**SelfEvo** is an advanced Android application that transforms everyday self-improvement and habit building into a gamified experience inspired by **EA FIFA Ultimate Team**. Instead of a standard boring checklist, users build and evolve their own **SelfEvo Player Card** by completing daily habits across attributes such as **Pace**, **Shooting**, **Passing**, **Dribbling**, **Defending**, and **Physical**.

---

## ✨ Core Features
1. **FIFA-Style Player Card Progression:**
   - Real-time overall rating (OVR) calculation based on weighted attributes.
   - Dynamic tier evolution (Bronze ➔ Silver ➔ Gold ➔ Walkout).
2. **Firebase Authentication:**
   - Firebase-managed email/password accounts and Google sign-in.
3. **Offline-First Architecture & Sync:**
   - Local data persistence via **Room Database**.
   - Per-user server persistence in **PostgreSQL**; API requests are authenticated with Firebase ID tokens.
   - Automatic background synchronization (`WorkManager` + `SyncWorker`) when network connectivity is restored.
4. **Profile Photos and NoSQL:**
   - Profile images are stored in Firebase Storage, with each signed-in user's image URL and update timestamp read from and written to their Cloud Firestore document.
5. **Push Notifications:**
   - Users opt in to daily-reminder and streak-alert Firebase Cloud Messaging topics from Settings.
6. **Multi-Language Localization:**
   - Full support for **English (`en`)**, **isiXhosa (`xh`)**, and **Afrikaans (`af`)**.
7. **REST API Backend (`selfevo-api`):**
   - Hosted in a separate repository: [`selfevo-api`](https://github.com/dimetri-softdev/selfevo-api).
   - Firebase-authenticated endpoints for user-scoped habits and player-card synchronization.
   - PostgreSQL persistence; see the API repository README for Neon/Render setup.

---

## 🛠️ Tech Stack
- **UI Framework:** Jetpack Compose & Material Design 3
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines & Flow
- **Local Storage:** Android Room Database
- **Cloud NoSQL and Blob Storage:** Firebase Cloud Firestore and Firebase Storage
- **Networking:** Retrofit 2, Gson, and OkHttp
- **Background Tasks:** WorkManager & Firebase Cloud Messaging (FCM)
- **Testing:** JUnit 4 & AndroidX Compose UI Test Framework

---

## Design and Data Flow

SelfEvo uses a Jetpack Compose interface organized around an MVVM pattern. The navigation exposes the dashboard, habit creation, evolution progress, and settings. Completing habits updates the locally cached player card immediately; the repository queues unsuccessful server writes and WorkManager retries synchronization when connectivity returns.

The authenticated REST API remains the source of truth for habits and player-card data in PostgreSQL. Firebase Authentication supplies the user's identity token to that API. Room holds the offline cache and retry queue. Profile photos use Firebase Storage, while the matching download URL is stored in the signed-in user's Firestore document. FCM topics deliver opt-in push alerts. Firebase rules must restrict profile documents and images to their owner's UID, as shown in the setup instructions below.

The interface uses the player-card metaphor to make progress visible, with English, Afrikaans, and isiXhosa string resources. Settings include language and appearance preferences, cloud sync, profile photos, and push-alert subscriptions.

## Part 3 Release Notes

See [RELEASE_NOTES.md](./RELEASE_NOTES.md) for changes made for the final submission.

## Demonstration Video

**Submission action required:** add the unlisted demonstration-video URL here after recording the voice-over walkthrough on a physical Android phone. The video should show sign-in, settings, the API/database data, offline sync, and the new profile-photo and push-notification features.

## 🚀 Getting Started & Installation

### 1. Android Application (`SelfEvo`)
1. Clone the repository:
   ```bash
   git clone https://github.com/dimetri-softdev/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-.git
   ```
2. Open the project in **Android Studio** (Koala / Jellyfish or newer).
3. Sync Gradle and run the app on an Android emulator or physical device (Min SDK 29, Target SDK 36).
4. To point builds at a different API deployment, pass its HTTPS base URL (including the trailing slash) as `-PselfEvoApiBaseUrl=https://your-api.example/`.
5. Enable Email/Password and Google sign-in in Firebase Authentication. Add the Android app's signing certificate SHA-1 and SHA-256 fingerprints in Firebase project settings, configure a Web OAuth client, and download the updated `google-services.json`. Google sign-in uses the `default_web_client_id` generated from that file; if it is absent, the app uses the `googleWebClientId` Gradle property. The provided client ID in the root `gradle.properties` is a public OAuth identifier, not a secret. Replace it with your Firebase project's Web client ID when configuring a different Firebase project, or pass it as `-PgoogleWebClientId=<client-id>`.
6. Enable **Cloud Firestore** and **Firebase Storage** in the same Firebase project to use profile photos. The Settings screen uploads the selected image to `profile-images/{uid}/profile-image` and reads/writes its download URL in the signed-in user's `users/{uid}` Firestore document. Images must be image MIME types and no larger than 5 MB.

   Publish owner-only rules in Firebase Console.

   **Firestore rules**

   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
     }
   }
   ```

   **Firebase Storage rules**

   ```javascript
   rules_version = '2';
   service firebase.storage {
     match /b/{bucket}/o {
       match /profile-images/{userId}/profile-image {
         allow read: if request.auth != null && request.auth.uid == userId;
         allow write: if request.auth != null
           && request.auth.uid == userId
           && request.resource.size <= 5 * 1024 * 1024
           && request.resource.contentType.matches('image/.*');
       }
     }
   }
   ```

   In Firebase Cloud Messaging, send test messages to the `selfevo_daily_reminders` or `selfevo_streak_alerts` topic after a user opts in from Settings. The app requests notification permission on Android 13 and later.
7. Read the [AI usage and citation report](./AI_USAGE.md) and keep it accurate for the tools used by the team.

### 2. Standalone REST API Backend (`selfevo-api`)
1. Clone the backend repository:
   ```bash
   git clone https://github.com/dimetri-softdev/selfevo-api.git
   ```
2. Install dependencies. Configure `DATABASE_URL` and Firebase Admin credentials as described in the API README, then start the server:
   ```bash
   npm ci
   npm start
   ```
3. The local server runs on `http://localhost:3000`.

---

## 🧪 Testing
Run unit tests and instrumentation tests via Gradle:
```bash
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
```

The GitHub Actions workflow runs unit tests and builds a debug APK on pushes and pull requests. A debug APK is not a Play Store release artifact; configure and verify release signing, create a signed AAB, and collect the Play Console evidence before publishing.
