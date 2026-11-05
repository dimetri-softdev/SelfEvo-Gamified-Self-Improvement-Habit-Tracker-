# SelfEvo Part 3 Release Notes

## Final-submission additions

- Added Google sign-in support for a Web OAuth client ID supplied as a Gradle property, with the Firebase-generated client resource as a fallback.
- Added profile-photo upload to a private Supabase Storage bucket through the Firebase-authenticated API. The API derives each object path from the verified Firebase UID and returns one-hour signed read URLs; Firestore stores the user's object path and update timestamp.
- Added JPEG, PNG, and WebP validation and a 5 MB upload limit. The Supabase service-role key remains server-side and must be configured as a secret on the API host.
- Connected the daily-reminder and streak-alert settings to opt-in Firebase Cloud Messaging topics.
- Added the Android 13+ notification-permission request and localized upload/notification status messages in English, Afrikaans, and isiXhosa.
- Fixed Google SSO on the sign-up screen to use Firebase Google authentication instead of submitting placeholder email/password credentials.
- Added a localized “Forgot password?” action to the login screen that sends a Firebase password-reset email to a valid entered email address.
- Expanded the README with data flow, Firebase setup and security rules, and final demonstration guidance.

## Before submission

- Complete the rubric-specific evidence checklist below using the provided POE Part 3 rubric.
- The demonstration video is now linked in the [README](./README.md).
- Remaining device evidence: offline recovery, real FCM notification delivery, language switching, and final card/attribute progression on the latest build.
- The latest code passes `:app:testDebugUnitTest` and builds a debug APK; the signed release APK and AAB listed below predate these latest code changes.
- Review all screenshots/video for personal details before submission. No Play Console upload has been made; do not upload there unless the course explicitly requires it.

## Release build and Play Store preparation

The Android release version is currently `1.0` (`versionCode` `1`) with application ID `com.example.selfevo`. Confirm that this is the intended Play Console application and version before publishing; do not change the application ID or signing key after creating the Play app.

Release signing reads credentials from these local environment variables:

- `SELFEVO_RELEASE_STORE_FILE` — path to the release keystore (keep it outside the repository).
- `SELFEVO_RELEASE_STORE_PASSWORD`
- `SELFEVO_RELEASE_KEY_ALIAS`
- `SELFEVO_RELEASE_KEY_PASSWORD`

Do not commit the keystore or passwords. The repository ignores `.jks` and `.keystore` files. If the app has ever been uploaded to Play Console, locate and reuse the original upload key; do not create a replacement. Back up the keystore and its passwords securely.

After confirming the correct signing key, build a signed Play bundle with `.\gradlew.bat bundleRelease` (Windows) or `./gradlew bundleRelease` (macOS/Linux). The AAB is written to `app/build/outputs/bundle/release/app-release.aab`. The current release disables code/resource shrinking because the previous minified release failed in Retrofit generic response parsing. Verify the rebuilt bundle signature and test the APK produced from the same configuration on a physical device. Capture genuine screenshots from that release build.

### Part 3 evidence checklist

- [x] **App runs on a mobile device (5 marks):** release APK installed and launched on the Samsung SM-A566B.
- [x] **SSO login (10 marks):** user confirmed Google sign-in succeeds in the installed signed release APK.
- [ ] **Offline mode (10 marks):** implementation includes local Room storage and queued sync; offline use and recovery have not been demonstrated on the final release.
- [ ] **Real-time notification (10 marks):** FCM topic subscriptions and notification-permission handling are implemented; end-to-end notification delivery has not been verified on the final release.
- [ ] **Multi-language (10 marks):** English, Afrikaans, and isiXhosa resources are present; language switching and translated screens still need a device check/evidence.
- [ ] **User Defined 4 (10 marks; Part 1 Planning and Design, "Custom Card Image Unlocks"):** profile-image upload was verified on the installed release using a neutral test image; see `Part3Evidence/settings-profile-photo-upload-success.png`. Verify tier progression with the photo and capture final card evidence.
- [ ] **User Defined 5 (10 marks; Part 1 Planning and Design, "Habit & Attribute Management" and "One tap-a-day interface"):** custom habit-to-card-attribute categories with one-tap daily logging. Verify habit setup, attribute assignment, and completion updating progress on the final release; capture evidence. This maps the two selected features that are not separately named by the rubric; confirm the lecturer expects this mapping.
- [ ] **Play Store preparation (5 marks):** rebuild and verify the final signed AAB and prepare the required store materials.
- [x] **AI-use write-up (5 marks):** [AI_USAGE.md](./AI_USAGE.md) documents tools, use cases, review, and validation; confirm the final report matches the whole team’s use.
- [ ] **User interface (10 marks):** capture and review genuine screens for consistency, usability, layout, fonts, and colours.
- [x] **GitHub, README, and automated testing (10 marks):** repository history, README, CI workflow, and automated tests are present; Android unit tests passed on 2026-11-05.
- [x] **Demonstration video (5 marks):** video link is in the [README](./README.md); confirm it is accessible to the assessor.
- [x] Build, sign, and install the release APK on a physical Samsung SM-A566B; installed app reports version `1.0` (`versionCode` `1`).
- [x] Verify Google sign-in using the installed signed release APK after updating the Firebase Android OAuth fingerprint and configuration; user confirmed successful sign-in on 2026-11-05.
- [x] Record the current installed release APK checksum and signing certificate fingerprints below.
- [x] Capture profile-photo upload and successful manual sync evidence with account-identifying content excluded; files are in the private `Part3Evidence` folder.
- [ ] Rebuild and verify the signed AAB after the latest auth configuration and release build changes. The existing AAB below is stale and must not be submitted as the final bundle.
- [x] Re-test profile-photo upload and manual cloud sync on the latest release APK. A neutral profile image appeared after upload, and Settings advanced the last-synced time to 22:02. Redacted screenshots are in the private `Part3Evidence` folder.
- [ ] Capture and review genuine screenshots: sign-in, dashboard/player card, completed-habit progress, evolution/theme selection, settings/profile photo, and notification settings.
- [ ] Test offline behavior and sync recovery if required by the formal rubric; capture a genuine recording or supplementary screenshots.
- [ ] Keep submission evidence private and remove/redact personal details, email addresses, notifications, and tokens as appropriate.
- [ ] Record any additional evidence filenames/results here after capturing them.

Current release APK evidence captured on 2026-11-05:

- Signed release APK: `app/build/outputs/apk/release/app-release.apk`
- Release version: `1.0` (`versionCode` `1`); application ID: `com.example.selfevo`
- APK build timestamp: 2026-11-05 21:25 (local machine time); installed on the Samsung SM-A566B at 21:29.
- APK SHA-256: `382F41CA5D7B95921BE8BCCF10AF5809E5804D35B2F1AF54CB51D8A7404C5FA0`
- APK signature verification: passed with Android SDK `apksigner`; verified using APK Signature Scheme v2.
- Release signing certificate SHA-1: `D7:A5:55:17:AB:15:97:5E:57:A4:FC:7F:9C:F4:47:E1:7F:8D:51:0C`
- Release signing certificate SHA-256: `2E:E7:61:F9:E0:B1:99:09:81:0B:C9:1E:A7:A1:7A:D1:DB:3E:18:49:08:19:F3:BF:00:4A:DB:99:68:21:BC:88`
- Firebase OAuth configuration: current release SHA-1 is present in `app/google-services.json`; Google sign-in succeeded on the installed release per the user's device test.
- Automated validation: `:app:compileReleaseKotlin` and `:app:testDebugUnitTest` passed; `npm test` in `selfevo-api` passed all 27 tests on 2026-11-05.
- Device-flow evidence: `Part3Evidence/settings-profile-photo-upload-success.png` shows the neutral test image after upload; `Part3Evidence/settings-cloud-sync-success.png` shows the manual sync success timestamp. The test image is currently the profile photo; select the desired final photo before submission.

Existing AAB evidence (not final; rebuild required):

- AAB: `app/build/outputs/bundle/release/app-release.aab`
- Build timestamp: 2026-11-05 15:51 (local machine time), before the final release build changes.
- SHA-256: `BF63B74EBE275E41C10CB9ADE11737CC3FEE3B3614BC193DB600EE8F82F9B85C`
- Previous signature check passed with `jarsigner -verify`; the certificate is self-signed and not publicly trusted.
