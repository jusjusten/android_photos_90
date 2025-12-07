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

## How to Compile and Run on Emulator

### Quick Start (Recommended)

**Step 1: Set Android SDK Path**
```powershell
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
```

**Step 2: Start the Emulator**
```powershell
# Start Pixel 6 API 36 emulator
Start-Process "$env:ANDROID_SDK_ROOT\emulator\emulator.exe" -ArgumentList "-avd", "Pixel_6_API_36" -WindowStyle Normal

# Wait for emulator to boot (takes about 30 seconds)
Start-Sleep -Seconds 25
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" wait-for-device
```

**Step 3: Build and Install the App**
```powershell
# Build and install in one command
.\gradlew.bat installDebug
```

**Step 4: Launch the App**
```powershell
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell am start -n com.softmeth.androidphotos/.MainActivity
```

### Alternative: Build and Install Separately

- To build a debug APK on Windows PowerShell from the project root:

```powershell
.\gradlew.bat assembleDebug
```

- To install the debug APK onto a running emulator:

```powershell
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" install -r app\build\outputs\apk\debug\app-debug.apk
```

### Using Android Studio

1. Open the project in Android Studio
2. Select "Pixel_6_API_36" from the device dropdown
3. Click the green "Run" ▶️ button (or press Shift+F10)
4. Android Studio will automatically build, install, and launch the app

### Adding Test Photos to Emulator

**Method 1: Drag and Drop**
- Simply drag any `.jpg` or `.png` file from your computer onto the emulator window
- Photos will be saved to `/sdcard/Download/`

**Method 2: Using ADB**
```powershell
# Push a photo to the emulator
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" push "C:\path\to\photo.jpg" /sdcard/Pictures/

# Trigger media scan to make it visible in gallery
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell am broadcast -a android.intent.action.MEDIA_MOUNTED -d file:///sdcard/Pictures
```

**Method 3: Download Sample Photos**
```powershell
# Download and push 5 sample photos
$tempDir = "$env:TEMP\android_test_photos"
New-Item -ItemType Directory -Force -Path $tempDir | Out-Null

$urls = @(
    "https://picsum.photos/800/600?random=1",
    "https://picsum.photos/800/600?random=2",
    "https://picsum.photos/800/600?random=3",
    "https://picsum.photos/800/600?random=4",
    "https://picsum.photos/800/600?random=5"
)

for ($i = 0; $i -lt $urls.Length; $i++) {
    $outFile = "$tempDir\sample_photo_$($i+1).jpg"
    Invoke-WebRequest -Uri $urls[$i] -OutFile $outFile -TimeoutSec 10
}

# Push to emulator
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
Get-ChildItem -Path $tempDir -Filter *.jpg | ForEach-Object {
    & "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" push $_.FullName /sdcard/Pictures/$($_.Name)
}

# Trigger media scan
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell am broadcast -a android.intent.action.MEDIA_MOUNTED -d file:///sdcard/Pictures
```

### Useful Commands

**Check if emulator is running:**
```powershell
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" devices
```

**Stop the app:**
```powershell
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell am force-stop com.softmeth.androidphotos
```

**Clear app data (reset to fresh state):**
```powershell
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell pm clear com.softmeth.androidphotos
```

**View app logs:**
```powershell
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" logcat -s AndroidPhotos:V
```

**Rebuild and reinstall:**
```powershell
.\gradlew.bat clean installDebug
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
