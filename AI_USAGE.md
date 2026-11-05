# AI Usage and Citation Report

**Course Module:** OPSC63212 - Mobile Development
**AI tools used:** Android Studio Gemini [1]; Copilot SDK in VS Code [2]

---

## 1. Summary of AI Use
AI tools supported debugging, implementation, and documentation during development. They were used as assistants rather than as substitutes for the team's design decisions or testing. Team members reviewed and edited suggestions and remain responsible for the submitted code, citations, and assessment evidence.

## 2. Key Use Cases
*   **Debugging and configuration**: Android Studio Gemini was used for earlier Gradle/Kotlin troubleshooting. Copilot helped inspect and update the Google OAuth configuration and sign-in implementation, including the signup flow and Credential Manager fallback.
*   **Feature implementation**: Copilot assisted with the profile-photo flow using private Supabase Storage through the Firebase-authenticated API and Cloud Firestore, FCM topic subscriptions, Android notification permission handling, and password reset.
*   **Release troubleshooting**: Copilot helped diagnose a release-only Retrofit parsing failure and update the release build configuration to avoid the failing minification/shrinking path.
*   **Documentation**: AI-assisted edits were made to the README, release notes, and this report.
*   **Human review and validation**: Team members reviewed and edited generated suggestions. Android Kotlin compilation and unit tests passed on 2026-11-05. The user confirmed Google sign-in on the installed signed release APK. Profile-photo upload, cloud sync, offline recovery, and end-to-end notification delivery still require final-release verification; they are not reported as passed here.

## 3. Conclusion
AI assistance was used to support development, and the team reviewed the resulting work. The report distinguishes completed build/test checks from device flows that remain unverified. The team is responsible for ensuring the final submission and evidence accurately represent the application's behavior.

## References

1. Android Developers. "Gemini in Android Studio." https://developer.android.com/studio/gemini (accessed 2026-11-05).
2. GitHub. "GitHub Copilot." https://github.com/features/copilot (accessed 2026-11-05).
