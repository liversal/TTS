# Silent TTS

A minimal Android text-to-speech (TTS) engine that is deliberately silent.

Silent TTS registers with Android as a normal `TextToSpeechService`. When an app or an accessibility service sends it an utterance, it starts and completes the TTS callback **without sending any audio frames**. It does not lower the device volume and does not mute music, notifications, calls, or other apps.

## Compatibility

- Minimum Android version: Android 8.0 (API 26)
- Target Android version: Android 14 (API 34)
- Language selection: accepts any non-empty language code; no voice or language data needs to be downloaded.
- Runtime permissions: none.

## Build

Requirements: JDK 17 and Android SDK Platform 34.

Windows:

```bat
gradlew.bat assembleDebug
```

macOS / Linux:

```sh
./gradlew assembleDebug
```

The debug APK is generated at:

```
app/build/outputs/apk/debug/app-debug.apk
```

Open the repository root in Android Studio (Hedgehog or newer), allow Gradle sync, then select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## Install and activate

1. Install `app-debug.apk` on the Android device.
2. Open Android Settings and search for **Text-to-speech output** (on Samsung, it is usually under General management).
3. Open **Preferred engine** and select **Silent TTS**.
4. If TalkBack is the intended client, make sure it uses the system preferred TTS engine, then test it.

The engine can also be selected by any app that uses Android's standard TTS framework.

## Important behavior

- No synthesized speech is produced. Each request completes immediately with an empty audio stream.
- This is not a volume controller. Other device audio remains unaffected.
- Text is not spoken, saved, transmitted, or logged by this engine.
- This project does not modify TalkBack or any other application.

## Project structure

- `SilentTtsService.java`: Android TTS service and silent synthesis callback.
- `CheckTtsDataActivity.java`: reports that no downloadable voice data is required.
- `MainActivity.java`: installation and activation instructions.

## License

MIT.