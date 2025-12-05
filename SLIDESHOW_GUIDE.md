# Photo Slideshow - Quick Reference

## How to Use the Slideshow Feature

### Opening the Slideshow
1. Launch the app
2. Tap on an album
3. Tap on any photo → **Slideshow mode activates**

### Slideshow Controls

```
┌─────────────────────────────────────┐
│                                     │
│         PHOTO DISPLAYED HERE        │
│          (Full Screen)              │
│                                     │
├─────────────────────────────────────┤
│  [Previous]         [Next]          │
│                                     │
│  Photo X / Y                        │
├─────────────────────────────────────┤
│  File Name: photo_name.jpg          │
│                                     │
│  Tags:                              │
│  • person: John Doe                 │
│  • location: New York               │
│                                     │
│  [+ Add Tag (menu)]                 │
└─────────────────────────────────────┘
```

### Button Behavior
- **Previous Button**: 
  - Goes to the previous photo in the album
  - Disabled when viewing the first photo
  
- **Next Button**: 
  - Goes to the next photo in the album
  - Disabled when viewing the last photo

### Features Available in Slideshow Mode
✅ View full-screen photo
✅ Navigate backward (Previous button)
✅ Navigate forward (Next button)
✅ See current position (e.g., "3 / 10")
✅ View photo filename
✅ View all tags on the photo
✅ Add new tags (menu option)
✅ Remove tags (tap on a tag)

### Implementation Details

**File**: `PhotoDetailActivity.java`

**Key Methods**:
- `showPreviousPhoto()` - Decrements position and displays previous photo
- `showNextPhoto()` - Increments position and displays next photo
- `displayCurrentPhoto()` - Updates the UI with current photo data

**Navigation Logic**:
```java
private void showPreviousPhoto() {
    if (currentPosition > 0) {
        currentPosition--;
        displayCurrentPhoto();
    }
}

private void showNextPhoto() {
    if (currentPosition < album.getPhotos().size() - 1) {
        currentPosition++;
        displayCurrentPhoto();
    }
}
```

**Button State Management**:
```java
btnPrevious.setEnabled(currentPosition > 0);
btnNext.setEnabled(currentPosition < album.getPhotos().size() - 1);
```

### Exit Slideshow
- Tap the back button (←) in the toolbar
- This returns you to the album view

## Album Management Features

### Add Photo to Album
- In album view, tap the **Floating Action Button (FAB)** with + icon
- Select a photo from your gallery
- Photo is added to the album

### Remove Photo from Album
- Long press on a photo thumbnail
- Select **"Remove"** or **"Delete"**
- Confirm deletion
- Photo is removed from the album

### Move Photo Between Albums
- Long press on a photo (in AlbumDetailActivity)
- Select **"Move to Album"**
- Choose destination album
- Photo moves to the new album

## Data Persistence
- All changes are automatically saved
- Album data persists across app restarts
- Photo URI permissions are maintained
- Tags are stored with each photo

## Code Architecture

### Activity Flow
```
MainActivity
    └─> AlbumDetailActivity / AlbumActivity
            └─> PhotoDetailActivity (Slideshow Mode)
```

### Data Flow
```
User Action → Activity → Album Model → Photo Model
                ↓
            DataManager
                ↓
        Persistent Storage
```

## Troubleshooting

**Photos not loading?**
- Check if URI permissions are granted
- Verify photo still exists on device
- Try re-adding the photo

**Slideshow buttons not working?**
- Ensure album has multiple photos
- Buttons auto-disable at first/last photo
- This is normal behavior

**Changes not saving?**
- Changes save automatically in `onPause()`
- Data loads in `onResume()`
- No manual save required
