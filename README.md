# Silent TTS

A minimal Android text-to-speech (TTS) engine that is deliberately silent.

Silent TTS registers with Android as a normal `TextToSpeechService`. When an app or an accessibility service sends it an utterance, it completes the TTS callback **without sending any audio frames**. It does not lower the device volume and does not mute music, notifications, calls, or other apps.

## Compatibility

- Minimum Android version: Android 8.0 (API 26)
- Target Android version: Android 14 (API 34)
- No voice packages or runtime permissions are required.

## Build

Requirements: JDK 17, Android SDK Platform 34, Android Gradle Plugin 8.2.0, and Gradle 8.2.

1. Open the repository root in Android Studio Hedgehog or newer.
2. Allow Gradle sync to finish.
3. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Install and activate

1. Install the generated APK on the Android device.
2. Open Android Settings and search for **Text-to-speech output** (on Samsung, it is usually under **General management**).
3. Open **Preferred engine** and select **Silent TTS**.
4. If TalkBack is the intended client, ensure it uses the system preferred TTS engine, then test it.

## Important behavior

- No synthesized speech is produced. Each request completes with an empty audio stream.
- This is not a volume controller. Other device audio remains unaffected.
- Text is not spoken, saved, transmitted, or logged by this engine.
- This project does not modify TalkBack or any other application.

## Project structure

- `SilentTtsService.java`: Android TTS service and silent synthesis callback.
- `CheckTtsDataActivity.java`: reports that no downloadable voice data is required.
- `MainActivity.java`: installation and activation instructions.

## License

MIT.
