# SelfEvo – Gamified Self-Improvement & Habit Tracker

**Course Module:** OPSC6321 - Mobile Development POE (Part 2)
**Target Platform:** Android (Native)
**Tech Stack:** Jetpack Compose, Room DB, WorkManager, Firebase, Retrofit, ASP.NET Core API

---

## 🚀 Overview
**SelfEvo** is a high-fidelity habit tracker that turns personal growth into a competitive card collection experience. Inspired by FIFA’s Ultimate Team (FUT) mechanics, SelfEvo allows users to "evolve" their real-life stats—Pace, Physical, Shooting, Passing, Defending, and Skill—by completing daily habits.

As stats increase, the user's **Overall Rating (OVR)** grows, triggering "Walkout" animations and promoting the user through Bronze, Silver, and Gold tiers to reach Legendary status.

---

## ✨ Key Features

### 🔐 Secure Identity & Access
*   **Firebase Authentication**: Robust Email/Password registration and login.
*   **Strict Security Rules**: Registration requires a secure password (8+ chars, number, symbol, and 6 letters).
*   **Biometric Integration**: Support for Fingerprint and Face ID authentication.
*   **Show/Hide Password**: Intuitive UI for secure credential entry.

### ⚽ Gamified Evolution Logic
*   **FUT-Style Stats**: Habits are linked to specific player attributes.
*   **Instant Stat Growth**: Every logged habit grants a `+2` increase to the linked stat.
*   **Dynamic OVR & Tiers**: Real-time calculation of player rating and tier assignment.
*   **Walkout Animations**: High-impact, animated overlays triggered when reaching key OVR milestones.

### 📱 Premium UX & Design
*   **AMOLED Theme**: A true pitch-black design optimized for battery savings and high-contrast visuals.
*   **Skeleton Shimmers**: Professional loading states that mirror the final UI layout.
*   **Reactive Dashboard**: Instant updates to habit lists and card visuals without manual refreshing.
*   **High-Fidelity Animations**: Smooth transitions and bouncy spring animations throughout the app.

### 🌐 Globalisation & Offline-First
*   **Multi-Language Support**: Fully localized UI for **English (Default)**, **isiXhosa**, and **Afrikaans**.
*   **Offline Persistence**: Powered by Room DB (v4) to ensure the app works perfectly without an internet connection.
*   **WorkManager Sync**: Background engine that automatically synchronizes local data with the remote REST API when connectivity returns.

---

## 🛠 Tech Stack & Architecture

*   **UI Framework**: Jetpack Compose (100% Declarative UI)
*   **Language**: Kotlin (Modern Android Standards)
*   **Persistence**: Room Database (SQLite abstraction)
*   **Background Tasks**: WorkManager
*   **Networking**: Retrofit 2 & OkHttp
*   **Backend Interface**: ASP.NET Core REST API
*   **Cloud Services**: Firebase (Auth, Cloud Messaging)
*   **Architecture Pattern**: MVVM (Model-View-ViewModel) + Repository Pattern

---

## 👥 Team Members
*   **Dimetri Peters** (Lead UI/UX & Gamification Logic)
*   **Silindokuhle Gqukani** (Infrastructure, Firebase & Localization)
*   **Nelson De Vos** (Data Persistence & Repository Architecture)
*   **Garren Gabron** (API Integration & Cloud Synchronization)

---

## 🛠 Setup & Installation

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/dimetri-softdev/SelfEvo-Gamified-Self-Improvement-Habit-Tracker-.git
    ```
2.  **Firebase Configuration**:
    *   Ensure your `google-services.json` is placed in the `app/` directory.
    *   **CRITICAL**: Enable "Email/Password" authentication in your Firebase Console.
3.  **Build the Project**:
    *   Open the project in Android Studio (Ladybug or newer).
    *   Sync Gradle and run the `:app:assembleDebug` task.
4.  **Language Settings**:
    *   The app defaults to English. To change languages, navigate to **Settings** within the app.

---

## 🧪 Testing & CI
*   **Unit Tests**: Core repository and stat math are verified in `HabitRepositoryTest.kt`.
*   **GitHub Actions**: Automated CI workflow checks every push for build stability and test passes.

---

## 🤖 AI Usage
This project utilized AI tools for architectural guidance and debugging. Detailed documentation of AI usage can be found in the [AI_USAGE.md](./AI_USAGE.md) file.

---

## 🎥 Demo Video
https://youtu.be/WUvA8PJY7ps?si=BaWI-J-e8OGw4zy4

---

## 📄 License
This project was developed for academic purposes as part of the OPSC6321 module. All rights reserved.
