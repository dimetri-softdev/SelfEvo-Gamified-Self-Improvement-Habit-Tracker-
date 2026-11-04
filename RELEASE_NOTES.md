# SelfEvo Part 3 Release Notes

## Final-submission additions

- Added Google sign-in support for a Web OAuth client ID supplied as a Gradle property, with the Firebase-generated client resource as a fallback.
- Added profile-photo upload to a private Supabase Storage bucket through the Firebase-authenticated API. The API derives each object path from the verified Firebase UID and returns one-hour signed read URLs; Firestore stores the user's object path and update timestamp.
- Added JPEG, PNG, and WebP validation and a 5 MB upload limit. The Supabase service-role key remains server-side and must be configured as a secret on the API host.
- Connected the daily-reminder and streak-alert settings to opt-in Firebase Cloud Messaging topics.
- Added the Android 13+ notification-permission request and localized upload/notification status messages in English, Afrikaans, and isiXhosa.
- Expanded the README with data flow, Firebase setup and security rules, and final demonstration guidance.

## Before submission

- Enable Cloud Firestore, publish the owner-only rules, create the private Supabase Storage bucket, and configure the API host secrets as described in the READMEs.
- Confirm the Firebase Google provider and registered signing fingerprints on a physical phone.
- Record and link the voice-over demo, prepare a signed release bundle, and add the applicable Play Console evidence.
