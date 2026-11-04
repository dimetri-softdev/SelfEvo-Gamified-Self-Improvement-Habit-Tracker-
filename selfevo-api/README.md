# SelfEvo API

This repository contains the REST API used by the SelfEvo Android app. Firebase Authentication verifies user identity; PostgreSQL stores each user's habits, completion history, and player card.

## Local development

Requirements: Node.js 20 or newer and a PostgreSQL database.

1. Install dependencies:
   ```bash
   npm ci
   ```
2. Configure environment variables (never commit credentials):
   - `DATABASE_URL`: PostgreSQL connection string.
   - `FIREBASE_SERVICE_ACCOUNT_JSON`: Firebase Admin service-account JSON. Alternatively, set `FIREBASE_SERVICE_ACCOUNT_BASE64` to the base64-encoded complete JSON file; this avoids newline and quoting problems in deployment environment variables.
   - `FIREBASE_PROJECT_ID`: Firebase project ID (optional if included in the service-account JSON).
   - `SUPABASE_URL`: Supabase project URL.
   - `SUPABASE_SERVICE_ROLE_KEY`: Supabase service-role key. Keep it only in the API host's secret environment variables; never expose it to Android clients or commit it.
   - `SUPABASE_STORAGE_BUCKET`: optional private Storage bucket name; defaults to `profile-images`.
   - `DATABASE_SSL_MODE`: optional `disable`, `require`, or `verify-full`; production defaults to `verify-full`.
   - `PORT`: HTTP port (optional; defaults to `3000`).
3. Start the service:
   ```bash
   npm start
   ```

The database schema is created at startup. Check readiness at `GET /healthz`. API routes require a Firebase ID token in `Authorization: Bearer <token>`.

## Deploying to Render with PostgreSQL

1. Create a Render PostgreSQL database in the same region as the web service. Copy its internal connection string. A Neon database can also be used with its TLS-enabled connection string.
2. Deploy this repository as a Render **Web Service**, with `npm ci` as the build command and `npm start` as the start command. Select Node.js 20 or newer.
3. Set these Render environment variables:
   - `DATABASE_URL` — the PostgreSQL connection string.
   - `FIREBASE_SERVICE_ACCOUNT_BASE64` — recommended: base64-encoded complete service-account JSON. Alternatively, `FIREBASE_SERVICE_ACCOUNT_JSON` can contain the complete JSON directly.
   - `FIREBASE_PROJECT_ID` — your Firebase project ID.
   - `NODE_ENV=production`
   - `DATABASE_SSL_MODE=require` — only when the database presents a self-signed certificate, as in the reported Render startup error. This still encrypts the connection, but skips server certificate verification. Prefer `verify-full` when the database provides a certificate chain trusted by Node.
4. Set the Render health-check path to `/healthz`.
5. Enable Email/Password and Google providers in Firebase Authentication. For Google sign-in, configure the Android app's SHA fingerprints and OAuth clients, then download an updated `google-services.json` containing the web OAuth client ID.
6. Set the Android app's API base URL to the deployed HTTPS service before releasing it.

Keep the service-account JSON and database URL private. Do not place either in the Android app, source control, logs, or public build artifacts. Firebase client configuration in the Android app is not a substitute for the Admin SDK credentials on the server.

## Profile image storage

Profile images use Supabase Storage rather than Firebase Storage, so the Android app does not require Firebase's paid Storage plan.

1. Create a Supabase project on its Free plan and create a **private** Storage bucket named `profile-images`.
2. Set the bucket's maximum file size to 5 MB and allowed MIME types to `image/jpeg`, `image/png`, and `image/webp`.
3. Add `SUPABASE_URL`, `SUPABASE_SERVICE_ROLE_KEY`, and (if using a different bucket) `SUPABASE_STORAGE_BUCKET` to the API host's secret environment settings, then redeploy the API. Do not place the service-role key in the Android app or source control. The API's service key bypasses Supabase Storage RLS.
4. The Android app uploads raw image bytes to `POST /api/profile-image` using its Firebase ID token. The API derives the object path from the verified Firebase UID, enforces the image MIME and 5 MB limits, and writes to `{uid}/profile-image` inside the configured bucket. `GET /api/profile-image` returns a signed read URL that expires after one hour. Firestore stores the bucket-relative image path and update timestamp in `users/{uid}`.
5. In Firebase Console, enable Cloud Firestore and publish owner-only rules for the `users/{uid}` profile document:

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

The Supabase Free plan currently lists 1 GB of file storage and 5 GB of egress, and free projects can pause after a week of inactivity. Limits and plan terms can change; confirm them in the [Supabase pricing page](https://supabase.com/pricing) before submission. The app/API still need to be tested end-to-end with the configured project.

If startup reports `self-signed certificate`, set `DATABASE_SSL_MODE=require` in Render's API service environment and redeploy. Production otherwise verifies the database certificate by default.

If startup reports `Failed to parse private key`, revoke any exposed service-account key and generate a new key. Prefer setting `FIREBASE_SERVICE_ACCOUNT_BASE64` to the base64-encoded complete JSON file in Render and remove the old `FIREBASE_SERVICE_ACCOUNT_JSON` variable. In PowerShell, create the value without printing the key with `$json = Get-Content -Raw "$HOME\Downloads\your-service-account-file.json"; [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($json)) | Set-Clipboard`, then paste the clipboard value into Render. Never paste the credential into chat or source control. Confirm `FIREBASE_PROJECT_ID` matches the JSON's `project_id`, save the variables, and redeploy.

## Endpoints

- `GET /healthz` — database readiness check; does not expose database details.
- `GET /api/habits?date=YYYY-MM-DD` — list the authenticated user's habits for that calendar date.
- `POST /api/habits` — create or idempotently synchronize one of the authenticated user's habits.
- `POST /api/habits/log` — record a completion with `habitId` and the user's local `completedOn` date; repeating the same request for the same date does not award stats again.
- `GET /api/sync/card` — get the authenticated user's player card.
- `PATCH /api/sync/card` — update the authenticated user's player name.
- `POST /api/profile-image` — upload a JPEG, PNG, or WebP profile image (maximum 5 MB) to the authenticated user's private Storage path.
- `GET /api/profile-image` — get a one-hour signed URL for the authenticated user's profile image.

The authenticated user ID is taken only from the verified Firebase token. The API does not accept passwords or issue its own login tokens.

## Tests

```bash
npm test
```
