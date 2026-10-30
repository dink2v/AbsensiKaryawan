# ABSEN CONTROL — Web Dashboard

A starter React/Vite dashboard that uses the same Firebase project as the Android attendance app. Current scope: allowlist-based admin login, live Firestore summary counts, and staff chat replies.

## Setup

1. In Firebase Console, open Project settings → Your apps and register a Web app if one does not exist.
2. Copy .env.example to .env.local and fill in the web app configuration values.
3. In Firebase Authentication, enable Email/Password.
4. Create controlAdmins/{uid} documents manually for approved admin UIDs. A minimal document such as { "enabled": true } is enough. Do not create these records from client code.
5. Review and test the proposed Firestore Rules in the separate security pull request in Firebase Emulator or a staging project first.
6. Install and run:

   ```sh
   npm install
   npm run dev
   ```

7. Build for deployment:

   ```sh
   npm run build
   ```

## Important

- This is a first dashboard scaffold, not a completed production release.
- Do not deploy Firestore Rules until attendance, approval, notifications, QR, user, and chat flows have been tested. The proposed rules preserve broad permissions for non-chat collections and are not full security hardening.
- Android currently stores chat timestamps as millisecond integers; this dashboard writes the same format for compatibility.
- Only Firebase Auth users with a trusted controlAdmins/{uid} document are admitted by the dashboard.
- Firebase web configuration is client-side configuration, not a service-account secret. Never put admin private keys or service-account JSON in browser code or .env.local.
