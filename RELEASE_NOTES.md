# SelfEvo Part 3 Release Notes

## Final-submission additions

- Added Google sign-in support for a Web OAuth client ID supplied as a Gradle property, with the Firebase-generated client resource as a fallback.
- Added profile-photo selection and upload to Firebase Storage. The signed-in user's Firestore document stores and returns the image URL and update timestamp.
- Added image validation for image MIME types and a 5 MB upload limit. Firebase Storage rules must enforce the same limit and restrict access by user UID.
- Connected the daily-reminder and streak-alert settings to opt-in Firebase Cloud Messaging topics.
- Added the Android 13+ notification-permission request and localized upload/notification status messages in English, Afrikaans, and isiXhosa.
- Expanded the README with data flow, Firebase setup and security rules, and final demonstration guidance.

## Before submission

- Enable Cloud Firestore and Firebase Storage and publish the owner-only security rules described in the README.
- Confirm the Firebase Google provider and registered signing fingerprints on a physical phone.
- Record and link the voice-over demo, prepare a signed release bundle, and add the applicable Play Console evidence.
