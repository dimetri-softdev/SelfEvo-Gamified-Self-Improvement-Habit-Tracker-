# SelfEvo — Gamified Self-Improvement & Habit Tracker (OPSC63212 PoE)

[![Android CI](https://github.com/dimetri-softdev/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-/actions/workflows/android.yml/badge.svg)](https://github.com/dimetri-softdev/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-/actions)

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
4. **Multi-Language Localization:**
   - Full support for **English (`en`)**, **isiXhosa (`xh`)**, and **Afrikaans (`af`)**.
5. **REST API Backend (`selfevo-api`):**
   - Hosted in a separate repository: [`selfevo-api`](https://github.com/dimetri-softdev/selfevo-api).
   - Firebase-authenticated endpoints for user-scoped habits and player-card synchronization.
   - PostgreSQL persistence; see the API repository README for Neon/Render setup.

---

## 🛠️ Tech Stack
- **UI Framework:** Jetpack Compose & Material Design 3
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines & Flow
- **Local Storage:** Android Room Database
- **Networking:** Retrofit 2, Gson, and OkHttp
- **Background Tasks:** WorkManager & Firebase Cloud Messaging (FCM)
- **Testing:** JUnit 4 & AndroidX Compose UI Test Framework

---

## 🚀 Getting Started & Installation

### 1. Android Application (`SelfEvo`)
1. Clone the repository:
   ```bash
   git clone https://github.com/dimetri-softdev/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-.git
   ```
2. Open the project in **Android Studio** (Koala / Jellyfish or newer).
3. Sync Gradle and run the app on an Android emulator or physical device (Min SDK 29, Target SDK 36).
4. To point builds at a different API deployment, pass its HTTPS base URL (including the trailing slash) as `-PselfEvoApiBaseUrl=https://your-api.example/`.
5. Enable Email/Password and Google sign-in in Firebase Authentication. Add the Android app's signing certificate SHA fingerprints, configure a web OAuth client, and download the updated `google-services.json` before testing Google sign-in.

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
./gradlew testDebugUnit
./gradlew connectedAndroidTest
```
