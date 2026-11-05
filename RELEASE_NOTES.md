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

- Enable Cloud Firestore, publish the owner-only rules, create the private Supabase Storage bucket, and configure the API host secrets as described in the READMEs.
- Confirm the Firebase Google provider and registered signing fingerprints on a physical phone.
- Record and link the voice-over demo, prepare a signed release bundle, and add the applicable Play Console evidence.

## Release build and Play evidence

The Android release version is currently `1.0` (`versionCode` `1`) with application ID `com.example.selfevo`. Confirm that this is the intended Play Console application and version before publishing; do not change the application ID or signing key after creating the Play app.

Release signing reads credentials from these local environment variables:

- `SELFEVO_RELEASE_STORE_FILE` — path to the release keystore (keep it outside the repository).
- `SELFEVO_RELEASE_STORE_PASSWORD`
- `SELFEVO_RELEASE_KEY_ALIAS`
- `SELFEVO_RELEASE_KEY_PASSWORD`

Do not commit the keystore or passwords. The repository ignores `.jks` and `.keystore` files. If the app has ever been uploaded to Play Console, locate and reuse the original upload key; do not create a replacement. Back up the keystore and its passwords securely.

After confirming the correct signing key, build a signed Play bundle with `.\gradlew.bat bundleRelease` (Windows) or `./gradlew bundleRelease` (macOS/Linux). The AAB is written to `app/build/outputs/bundle/release/app-release.aab`. Verify the bundle signature and install a release APK built with the same signing configuration on a physical device. Capture genuine screenshots from that release build. Add the final build date, version, artifact signature verification, device-test result, screenshots, and applicable Play Console upload evidence here before submission.

### Part 3 evidence checklist

- [x] Record the release version, version code, build date, and SHA-256 checksum of the signed artifact.
- [x] Verify the AAB signature and save the verification output; never include keystore passwords or private signing material.
- [ ] Install the signed release APK on a physical phone and capture genuine screenshots of sign-in, the dashboard/player card, completed-habit progress, evolution/theme selection, settings/profile photo, and notification settings.
- [ ] After registering the release signing fingerprints and downloading updated Firebase configuration, verify Google sign-in using the signed release build.
- [ ] Capture a short, genuine screen recording or supplementary screenshots showing offline behavior and sync recovery, if those are rubric requirements.
- [ ] Keep evidence in a private submission/evidence folder; review screenshots for personal details, email addresses, tokens, and other data that should not be submitted.
- [ ] Play Console screenshots or upload status are applicable only if a Play Console account is available. Do not claim a Play upload or fabricate console evidence when it has not occurred.
- [ ] Add evidence filenames and a brief result for each tested flow to this section once captured.

Evidence captured on 2026-11-05:

- Signed Android App Bundle: `app/build/outputs/bundle/release/app-release.aab`
- Signed release APK: `app/build/outputs/apk/release/app-release.apk`
- Release version: `1.0` (`versionCode` `1`)
- Build timestamp: AAB 2026-11-05 15:51; APK 2026-11-05 15:58 (local machine time)
- Signature verification: passed with `jarsigner -verify`; output reported `jar verified`. The certificate is self-signed (expected for a locally generated upload key), is not publicly trusted, and has no timestamp; this verifies the bundle signature but is not Play Console upload evidence.
- AAB SHA-256: `BF63B74EBE275E41C10CB9ADE11737CC3FEE3B3614BC193DB600EE8F82F9B85C`
- APK signature verification: passed with Android SDK `apksigner`; verified using APK Signature Scheme v2 with one signer (`CN=SelfEvo Team`).
- APK SHA-256: `5B960E5A367FC4083142B275B5DB3CF10E5B7E490B5FE77B185D1F234B8668D6`
- Release signing certificate SHA-1 for Firebase Google sign-in: `D7:A5:55:17:AB:15:75:E5:7E:A4:FC:7F:9C:F4:47:E1:7F:8D:51:0C`
- Release signing certificate SHA-256 for Firebase Google sign-in: `2E:E7:61:F9:E0:B1:99:08:10:BC:B9:AD:E1:17:37:AD:1D:B3:E1:84:90:81:9F:3B:F0:04:AD:B9:96:82:1B:C8`
- Physical-device test: installed the signed release APK on a Samsung SM-A566B on 2026-11-05 and launched `MainActivity` successfully. No fatal app exception appeared in the checked device log. Installing required uninstalling the differently signed prior build, so local-only app data may have been cleared.
- Genuine in-app screenshots and testing of signed-in flows on the release build remain pending.
- Play Console evidence was not captured because a Play Console account is not available.
