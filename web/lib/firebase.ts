import { getApps, initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const config = {
  apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY,
  authDomain: process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
  appId: process.env.NEXT_PUBLIC_FIREBASE_APP_ID,
  messagingSenderId: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID,
  storageBucket: process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET,
};

export function getFirebaseClient() {
  const required = ["apiKey", "authDomain", "projectId", "appId"] as const;
  const missing = required.filter((key) => !config[key]);
  if (missing.length) {
    throw new Error("Konfigurasi Firebase Web belum lengkap. Admin perlu mengatur variabel NEXT_PUBLIC_FIREBASE_* di GitHub Actions Variables.");
  }

  const appName = "absensi-karyawan-web-auth";
  const app = getApps().find((item) => item.name === appName) ?? initializeApp(config, appName);
  return { auth: getAuth(app), db: getFirestore(app) };
}
