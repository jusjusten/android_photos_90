# 📱 Testing Guide - Android Photos App

## ✅ Back Button Locations

### AlbumDetailActivity (Main album view)
- **Location**: Top-left corner of the screen
- **Type**: ActionBar back arrow (←)
- **Code**: Already implemented in `AlbumDetailActivity.java`
  ```java
  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  ```
- **Action**: Returns to MainActivity (album list)

### AlbumActivity (Alternative album view)
- **Location**: Top-left corner of the MaterialToolbar
- **Type**: Navigation icon (←)
- **Code**: Already implemented in `AlbumActivity.java`
  ```java
  toolbar.setNavigationOnClickListener(v -> finish());
  ```
- **Action**: Returns to previous screen

### PhotoDetailActivity (Slideshow view)
- **Location**: Top-left corner of the screen
- **Type**: ActionBar back arrow (←)
- **Code**: Already implemented
- **Action**: Returns to album view

## 📸 Adding Photos to the Emulator

### Method 1: Using ADB (Automated - Already Done!)
✅ **5 sample photos have been pushed to your emulator**

Location: `/sdcard/Pictures/`
- `sample_photo_1.jpg`
- `sample_photo_2.jpg`
- `sample_photo_3.jpg`
- `sample_photo_4.jpg`
- `sample_photo_5.jpg`

### Method 2: Using the Emulator Camera
1. Open the emulator's Camera app
2. Take photos
3. They'll appear in the gallery automatically

### Method 3: Drag and Drop (Easiest!)
1. Find any image file on your computer (`.jpg`, `.png`)
2. Drag and drop it onto the emulator window
3. The emulator will save it to `/sdcard/Download/`
4. Access via the Files app or Gallery

### Method 4: Push Custom Photos via ADB
```powershell
# Push a photo from your computer to the emulator
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" push "C:\path\to\your\photo.jpg" /sdcard/Pictures/

# Trigger media scan to make it visible
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell am broadcast -a android.intent.action.MEDIA_MOUNTED -d file:///sdcard/Pictures
```

### Method 5: Download from Web in Emulator
1. Open Chrome browser in the emulator
2. Navigate to any website with images
3. Long press on an image → Save image
4. Image saved to Downloads folder

## 🧪 Complete Testing Workflow

### Step 1: Create an Album
1. Launch the app (already done!)
2. Tap the **+** (FAB) button
3. Enter album name: "Test Album"
4. Tap "Create"

### Step 2: Open the Album
1. Tap on "Test Album"
2. **✓ Verify**: Back arrow (←) appears in top-left
3. You should see "No photos" message

### Step 3: Add Photos
1. Tap the **+** (FAB) button in album view
2. Select "Photos" or "Gallery" app
3. Choose one or more of the 5 sample photos
4. **✓ Verify**: Photos appear in grid (3 columns)
5. **✓ Verify**: Photo count updates
6. Add at least 3-5 photos for proper slideshow testing

### Step 4: Test Slideshow Navigation
1. Tap on the **first photo**
2. **✓ Verify**: Photo opens full-screen
3. **✓ Verify**: [Previous] button is **disabled** (grayed out)
4. **✓ Verify**: [Next] button is **enabled**
5. **✓ Verify**: Position shows "1 / X" in toolbar
6. Tap **[Next]** button
7. **✓ Verify**: Photo changes to next one
8. **✓ Verify**: [Previous] is now **enabled**
9. **✓ Verify**: Position updates to "2 / X"
10. Navigate to the **last photo**
11. **✓ Verify**: [Next] button is **disabled**
12. **✓ Verify**: [Previous] button is **enabled**

### Step 5: Test Back Navigation
1. From slideshow view, tap the **← back arrow**
2. **✓ Verify**: Returns to album grid view
3. Tap the **← back arrow** again
4. **✓ Verify**: Returns to main album list

### Step 6: Test Photo Management
1. Open an album with photos
2. **Long press** on a photo
3. **✓ Verify**: Options dialog appears
4. Select "Delete" or "Remove"
5. Confirm deletion
6. **✓ Verify**: Photo is removed from grid
7. **✓ Verify**: Photo count updates

### Step 7: Test Tag Management (Bonus Feature)
1. Open a photo in slideshow mode
2. Tap **menu icon** (three dots) in toolbar
3. Select "Add Tag"
4. Choose tag type: "person" or "location"
5. Enter a value (e.g., "John Doe")
6. Tap "Add"
7. **✓ Verify**: Tag appears below photo
8. Tap on the tag
9. Confirm deletion
10. **✓ Verify**: Tag is removed

## 🎯 Quick Test Checklist

- [ ] Back button in AlbumDetailActivity works
- [ ] Back button in PhotoDetailActivity works
- [ ] Can create albums
- [ ] Can add photos from gallery
- [ ] Photos display in 3-column grid
- [ ] Can tap photo to open slideshow
- [ ] Previous button works (and disables at first photo)
- [ ] Next button works (and disables at last photo)
- [ ] Position indicator updates correctly
- [ ] Back button returns to album view
- [ ] Can remove photos
- [ ] Can add tags to photos
- [ ] Can delete tags
- [ ] Data persists after app restart

## 🔧 Troubleshooting

### Photos not appearing in gallery?
```powershell
# Trigger media scan
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell am broadcast -a android.intent.action.MEDIA_MOUNTED -d file:///sdcard/Pictures
```

### Need to restart the app?
```powershell
# Stop the app
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell am force-stop com.softmeth.androidphotos

# Start the app
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell am start -n com.softmeth.androidphotos/.MainActivity
```

### Need to clear app data?
```powershell
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" shell pm clear com.softmeth.androidphotos
```

## 📊 Sample Photos Provided

The following test images are already in the emulator at `/sdcard/Pictures/`:
- ✅ sample_photo_1.jpg
- ✅ sample_photo_2.jpg
- ✅ sample_photo_3.jpg
- ✅ sample_photo_4.jpg
- ✅ sample_photo_5.jpg

These are random images from Lorem Picsum (800x600 resolution).

## 🚀 Ready to Test!

Your emulator is running with:
- ✅ App installed
- ✅ App launched
- ✅ 5 sample photos in gallery
- ✅ Back buttons configured in all views
- ✅ All features ready to test

**Start testing by creating an album and adding some photos!**
