# HOPE AI V2.0

HOPE AI is a native Android personal-assistant application created by Yash
Pawar. It supports English, Hindi and Hinglish, a voice interface, text chat,
tasks, local reminders, controlled memory and secure hooks for Supabase, Mem0,
Gemini and Firebase Cloud Messaging.

## Current working build

- Native Kotlin + Jetpack Compose application
- Premium HOPE-branded dark interface
- Local demo mode
- Supabase email sign-in/sign-up when configured
- Secure backend chat hook
- Android speech recognition and text-to-speech loop
- Task persistence and notification test
- On-device Memory Manager with clear Mem0 connection status
- Settings, language choice and About screen
- Firebase Messaging service and token-registration hook
- Unit tests and GitHub Actions APK build

The APK never includes Gemini, Mem0, Supabase service-role or Firebase Admin
secrets. Production AI and Mem0 calls must run through a trusted backend.

## Android configuration

Create `local.properties` from `local.properties.example` and retain the normal
Android `sdk.dir` entry:

```properties
sdk.dir=C:\\Users\\YOUR_NAME\\AppData\\Local\\Android\\Sdk
HOPE_SUPABASE_URL=https://YOUR_PROJECT.supabase.co
HOPE_SUPABASE_ANON_KEY=YOUR_PUBLIC_ANON_KEY
HOPE_BACKEND_URL=https://YOUR_SECURE_BACKEND.example.com
```

The backend contract is:

- `POST /chat` with `{ "message": "...", "language": "hinglish" }`
- `POST /devices/register` with `{ "token": "...", "platform": "android" }`
- `Authorization: Bearer <Supabase access token>` for authenticated requests

## Firebase notifications

In Firebase Console, create an Android app with the exact package:

```text
com.yashpawar.hopeai
```

Download its `google-services.json` and place it at:

```text
app/google-services.json
```

The project package includes the verified Firebase Android config for
`com.yashpawar.hopeai`. Restrict its Firebase API key to the required Android
application and APIs before production release. A config created for another
package will not work.

## Build

```bash
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
```

APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Every push to `main` also runs Android CI and uploads the debug APK as the
`HOPE-AI-V2-debug-apk` workflow artifact.

On Windows, `PUSH_AND_BUILD.bat` can push the extracted project to the configured
GitHub repository. Git for Windows must be installed and authenticated.

## Important status

The app works locally without cloud keys in Local Demo mode. Real Gemini,
Gemini Live, Mem0, Google Calendar, Gmail and WhatsApp require their backend
services and OAuth configuration. They are not simulated as successful inside
the APK.
