# Android Photos (port of JavaFX Photos)

This project is an Android port of the JavaFX Photos app. The repository uses the Kotlin DSL Gradle files (`build.gradle.kts`) and Java source files for the app logic.

Goals for initial setup
- Open the project in Android Studio (use the default Kotlin-DSL option shown by the wizard).
- Create a Pixel 6 AVD with API level 36 and resolution 1080 x 2400 (420 dpi).
- Build and run the app on that emulator.

Required emulator configuration
- Device: Pixel 6 (or any device with resolution 1080 x 2400 and 420 dpi)
- API level: 36
- ABI: x86_64 (recommended for emulator performance)

Opening in Android Studio
1. Launch Android Studio.
2. Choose "Open" and select this folder: `C:\Users\Ginod\OneDrive\Desktop\Soft_meth\android_photos_90`.
3. Let Gradle sync. The project uses Java 11 compatibility (see `app/build.gradle.kts`).

Creating an AVD (recommended via command line)
Note: You can create AVDs with the Android Studio AVD Manager GUI. The commands below are PowerShell-friendly alternatives if you have the Android SDK command-line tools installed and `sdkmanager`, `avdmanager`, and `emulator` are on your PATH (or use the full SDK path).

PowerShell example (adjust `ANDROID_SDK_ROOT` if needed):

```powershell
# Install system image for API 36 (if not installed)
& "$env:ANDROID_SDK_ROOT\cmdline-tools\latest\bin\sdkmanager" "system-images;android-36;google_apis;x86_64"

# Create Pixel 6 AVD named Pixel_6_API_36
& "$env:ANDROID_SDK_ROOT\cmdline-tools\latest\bin\avdmanager" create avd -n Pixel_6_API_36 -k "system-images;android-36;google_apis;x86_64" -d "pixel_6"

# Start the emulator (may take a minute)
& "$env:ANDROID_SDK_ROOT\emulator\emulator.exe" -avd Pixel_6_API_36
```

If `ANDROID_SDK_ROOT` is not set, point to your SDK path (for example: `C:\Users\<you>\AppData\Local\Android\Sdk`).

Build and run from command-line
- To build a debug APK on Windows PowerShell from the project root:

```powershell
.\gradlew.bat assembleDebug
```

- To install the debug APK onto a running emulator (adb must be on PATH):

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Notes and constraints
- This project is implemented using Java (the `app/src/main/java` folder contains Java source files). Do not convert code to Kotlin — the assignment requires Java only.
- The Gradle files use the Kotlin DSL (`build.gradle.kts`) which is the required build configuration for this course assignment.
- If you plan to use GenAI (ChatGPT / Claude / Gemini), keep a README entry describing prompts and generated code — the course requires documenting GenAI usage.

What's next
- Once you can build and run the app in the Pixel 6 API 36 emulator, I will implement the Home screen UI that lists albums and loads saved data on startup.

Troubleshooting
- Gradle sync fails: ensure Android Studio has an appropriate JDK (11) configured and the Android SDK components for API 36 installed.
- Emulator doesn't start or is too slow: use an x86_64 system image and enable hardware acceleration (HAXM or Windows Hypervisor Platform).
