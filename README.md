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
2. **Google SSO & Secure Authentication:**
   - Encrypted email/password credentials and **Google Single Sign-On (SSO)**.
3. **Offline-First Architecture & Sync:**
   - Local data persistence via **Room Database**.
   - Automatic background synchronization (`WorkManager` + `SyncWorker`) when network connectivity is restored.
4. **Multi-Language Localization:**
   - Full support for **English (`en`)**, **isiXhosa (`xh`)**, and **Afrikaans (`af`)**.
5. **Standalone REST API Backend (`selfevo-api`):**
   - Hosted in a separate repository: [`selfevo-api`](https://github.com/dimetri-softdev/selfevo-api).
   - Endpoints for authentication, habit logging, and player card synchronization.

---

## 🛠️ Tech Stack
- **UI Framework:** Jetpack Compose & Material Design 3
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines & Flow
- **Local Storage:** Android Room Database & DataStore
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
3. Sync Gradle and run the app on an Android emulator or physical device (Min SDK 26, Target SDK 34+).

### 2. Standalone REST API Backend (`selfevo-api`)
1. Clone the backend repository:
   ```bash
   git clone https://github.com/dimetri-softdev/selfevo-api.git
   ```
2. Install dependencies & start server:
   ```bash
   npm install
   npm start
   ```
3. Server runs on `http://localhost:3000`.

---

## 🧪 Testing
Run unit tests and instrumentation tests via Gradle:
```bash
./gradlew testDebugUnit
./gradlew connectedAndroidTest
```
