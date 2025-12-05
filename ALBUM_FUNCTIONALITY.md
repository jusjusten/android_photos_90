# Album Functionality Documentation

## Overview
Your Android Photos app now has complete album management functionality with photo display and slideshow capabilities.

## Features Implemented

### 1. Opening an Album
- **Entry Point**: `MainActivity.java`
- **Action**: Click on any album from the main list
- **Implementation**: Opens `AlbumDetailActivity` which displays all photos in the album

### 2. Adding Photos to an Album
- **Location**: `AlbumDetailActivity.java` and `AlbumActivity.java`
- **Action**: Click the Floating Action Button (FAB) with the "+" icon
- **Implementation**: 
  - Opens the device's image picker
  - Allows selection of an image
  - Adds the selected photo to the album
  - Photo is saved with persistent URI permissions

### 3. Removing Photos from an Album
- **Location**: `AlbumDetailActivity.java` and `AlbumActivity.java`
- **Action**: Long press on a photo
- **Options Available**:
  - **View**: Opens the photo in detail view
  - **Move to Album**: Moves photo to another album (AlbumDetailActivity only)
  - **Remove/Delete**: Removes the photo from the album
- **Implementation**: Shows a dialog with photo options, confirms deletion before removing

### 4. Displaying Photos (Photo Detail View)
- **Activity**: `PhotoDetailActivity.java`
- **Entry Point**: Click on any photo thumbnail in an album
- **Features**:
  - Full-screen photo display
  - Shows photo filename
  - Displays all tags associated with the photo
  - Manual slideshow controls

### 5. Slideshow with Manual Controls
- **Location**: `PhotoDetailActivity.java`
- **Controls**:
  - **Previous Button**: Navigate to the previous photo in the album
  - **Next Button**: Navigate to the next photo in the album
- **Features**:
  - Buttons are automatically enabled/disabled based on position
  - Previous button disabled when at first photo
  - Next button disabled when at last photo
  - Position indicator shows "X / Y" (current photo / total photos)

### 6. Tag Management
- **Add Tags**: Menu option in `PhotoDetailActivity`
- **Tag Types**: Person or Location (simplified from JavaFX version)
- **Remove Tags**: Click on any tag to delete it
- **Persistence**: Tags are automatically saved and restored

## Code Flow

### Opening an Album and Viewing Photos
```
MainActivity (album list)
    ↓ (click album)
AlbumDetailActivity (photo grid)
    ↓ (click photo)
PhotoDetailActivity (photo display + slideshow)
```

### Key Files

1. **MainActivity.java**
   - Displays list of all albums
   - Opens `AlbumDetailActivity` when album is clicked

2. **AlbumDetailActivity.java**
   - Shows grid of photos in the selected album
   - Allows adding photos via FAB
   - Allows removing/moving photos via long press
   - Opens `PhotoDetailActivity` when photo is clicked

3. **AlbumActivity.java**
   - Alternative album view implementation
   - Similar functionality to AlbumDetailActivity
   - Now properly opens PhotoDetailActivity

4. **PhotoDetailActivity.java**
   - Full-screen photo display
   - Slideshow controls (Previous/Next buttons)
   - Tag management
   - Position indicator

5. **Models**
   - `Album.java`: Manages album data and photo list
   - `Photo.java`: Stores photo URI, filename, and tags
   - `Tag.java`: Represents photo tags

6. **DataManager.java**
   - Handles persistence of albums and photos
   - Saves/loads data to device storage

## User Workflow

### Viewing Photos in Slideshow Mode
1. Open the app (MainActivity)
2. Click on an album
3. Click on any photo to enter slideshow mode
4. Use "Previous" and "Next" buttons to navigate through photos
5. View photo details and tags while browsing

### Managing Photos
1. **Add Photo**: 
   - Open album → Click FAB → Select image from gallery
2. **Remove Photo**: 
   - Open album → Long press photo → Select "Remove" → Confirm
3. **View Photo**: 
   - Open album → Click photo (enters slideshow mode)
4. **Add Tags**: 
   - View photo → Click menu icon → Add Tag → Enter tag details
5. **Remove Tags**: 
   - View photo → Click on a tag → Confirm deletion

## Layout Files

- `activity_album_detail.xml`: Grid layout for photos in album
- `activity_photo_detail.xml`: Photo display with slideshow controls
- `item_photo.xml`: Individual photo thumbnail
- `item_tag.xml`: Tag display in photo detail

## Recent Updates

### AlbumActivity.java
- ✅ Implemented photo detail view opening (removed TODO)
- ✅ Added onResume() to refresh album data when returning from PhotoDetailActivity
- ✅ Ensures tags and photo changes are reflected when navigating back

## Testing Checklist

- [ ] Open an album from MainActivity
- [ ] Add a photo to an album
- [ ] Click a photo to view it full-screen
- [ ] Navigate forward through photos using "Next" button
- [ ] Navigate backward through photos using "Previous" button
- [ ] Verify buttons disable at first/last photo
- [ ] Add tags to a photo
- [ ] Remove tags from a photo
- [ ] Long press photo to see options
- [ ] Remove a photo from an album
- [ ] Verify photo count updates correctly
- [ ] Verify data persists after app restart

## Notes

- All photo and album data is automatically saved when leaving an activity
- URI permissions are requested to maintain access to photos
- The app uses a grid layout (3 columns) for photo display in albums
- Slideshow navigation is manual (no auto-advance timer)
- All functionality works with the simplified model (no dates, filename as caption)
