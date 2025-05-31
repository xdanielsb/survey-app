import { initializeApp } from 'firebase/app'
import { getAuth } from 'firebase/auth'
import type { FirebaseApp } from 'firebase/app'
import type { Auth } from 'firebase/auth'
import { logger } from '@/plugins/logger'
import { GoogleAuthProvider } from 'firebase/auth'

let app: FirebaseApp | null = null
let auth: Auth | null = null
let googleProvider: GoogleAuthProvider | null = null

try {
  const firebaseConfig = {
    apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
    authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
    projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
    storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
    messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
    appId: import.meta.env.VITE_FIREBASE_APP_ID,
    measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID,
  }

  app = initializeApp(firebaseConfig)
  auth = getAuth(app)

  googleProvider = new GoogleAuthProvider()
  googleProvider.setCustomParameters({
    // Forces account picker on every sign-in
    prompt: 'select_account',
  })
} catch (error) {
  logger.error(' Failed to initialize:' + JSON.stringify(error))
}

export { app, auth, googleProvider }
