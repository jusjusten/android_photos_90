# Album & Slideshow Feature - Complete Flow Diagram

## User Interaction Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        MAIN ACTIVITY                             │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Albums List                                              │  │
│  │  • Family Vacation (15 photos)                            │  │
│  │  • Birthday Party (8 photos)    <── Click to open        │  │
│  │  • Summer 2024 (23 photos)                                │  │
│  │                                                            │  │
│  │  [+] Create Album Button                                  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ (tap album)
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    ALBUM DETAIL ACTIVITY                         │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  "Birthday Party" Album                                   │  │
│  │  ┌────────┐  ┌────────┐  ┌────────┐                      │  │
│  │  │ Photo1 │  │ Photo2 │  │ Photo3 │  <── Click photo     │  │
│  │  └────────┘  └────────┘  └────────┘                      │  │
│  │  ┌────────┐  ┌────────┐  ┌────────┐                      │  │
│  │  │ Photo4 │  │ Photo5 │  │ Photo6 │  <── Long press      │  │
│  │  └────────┘  └────────┘  └────────┘      for options     │  │
│  │                                                            │  │
│  │  [+] Add Photo Button                                     │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                  │
│  Options on Long Press:                                         │
│  • View                                                          │
│  • Move to Album                                                 │
│  • Remove                                                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ (tap photo)
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   PHOTO DETAIL ACTIVITY                          │
│                      (SLIDESHOW MODE)                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                                                            │  │
│  │                                                            │  │
│  │                   [FULL SCREEN PHOTO]                     │  │
│  │                                                            │  │
│  │                                                            │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │  Slideshow Controls:                                      │  │
│  │  ┌──────────┐              ┌──────────┐                  │  │
│  │  │ Previous │    Photo 3/8  │   Next   │                  │  │
│  │  └──────────┘              └──────────┘                  │  │
│  │       ↑                          ↑                        │  │
│  │   Goes back              Goes forward                     │  │
│  │   (disabled at             (disabled at                   │  │
│  │    first photo)            last photo)                    │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │  File Name: IMG_20240315.jpg                              │  │
│  │                                                            │  │
│  │  Tags:                                                     │  │
│  │  • person: John Doe                                        │  │
│  │  • location: New York                                      │  │
│  │                                                            │  │
│  │  [Menu: Add Tag]                                          │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Feature Matrix

| Feature | Activity | UI Component | Status |
|---------|----------|--------------|--------|
| Open Album | MainActivity | Album List Item | ✅ |
| Add Photo | AlbumDetailActivity | FAB Button | ✅ |
| Remove Photo | AlbumDetailActivity | Long Press Menu | ✅ |
| Display Photo | PhotoDetailActivity | Image View | ✅ |
| Slideshow Previous | PhotoDetailActivity | Previous Button | ✅ |
| Slideshow Next | PhotoDetailActivity | Next Button | ✅ |
| Position Indicator | PhotoDetailActivity | Toolbar Subtitle | ✅ |
| Add Tags | PhotoDetailActivity | Menu Option | ✅ |
| Remove Tags | PhotoDetailActivity | Click Tag | ✅ |

## Code Implementation Map

```
MainActivity.java
    │
    ├─> openAlbum(Album) 
    │       │
    │       └─> Intent → AlbumDetailActivity
    │
    └─> onAlbumClick(Album, position)
            └─> openAlbum()

AlbumDetailActivity.java
    │
    ├─> addPhotoToAlbum(Uri)
    │       └─> album.addPhoto(photo)
    │
    ├─> removePhoto(Photo, position)
    │       └─> album.removePhoto(photo)
    │
    ├─> openPhoto(position)
    │       │
    │       └─> Intent → PhotoDetailActivity
    │               │
    │               ├─> EXTRA_ALBUM_NAME
    │               └─> EXTRA_PHOTO_POSITION
    │
    └─> onPhotoClick(Photo, position)
            └─> openPhoto(position)

PhotoDetailActivity.java
    │
    ├─> displayCurrentPhoto()
    │       │
    │       ├─> imageView.setImageURI(photo.getUri())
    │       ├─> update button states
    │       └─> update position indicator
    │
    ├─> showPreviousPhoto()
    │       │
    │       ├─> currentPosition--
    │       └─> displayCurrentPhoto()
    │
    ├─> showNextPhoto()
    │       │
    │       ├─> currentPosition++
    │       └─> displayCurrentPhoto()
    │
    ├─> showAddTagDialog()
    │       └─> photo.addTag(name, value)
    │
    └─> showDeleteTagDialog(Tag)
            └─> photo.removeTag(name, value)
```

## Data Persistence Flow

```
User Action
    ↓
Activity Method
    ↓
Model Update (Album/Photo)
    ↓
onPause() called
    ↓
DataManager.saveAlbums(context, albums)
    ↓
Serialized to File
    ↓
Saved to Internal Storage

------- App Restart -------

onCreate() called
    ↓
DataManager.loadAlbums(context)
    ↓
Read from Internal Storage
    ↓
Deserialize
    ↓
Restore Albums and Photos
```

## Slideshow Control Logic

```java
// Button state is determined by current position
┌─────────────────────────────────────────┐
│  Photo Position: currentPosition        │
│  Total Photos: album.getPhotos().size() │
└─────────────────────────────────────────┘
              │
              ├─> Previous Button Enabled?
              │   └─> currentPosition > 0
              │
              └─> Next Button Enabled?
                  └─> currentPosition < (total - 1)

Example:
  Album has 5 photos (indices 0-4)
  
  Position 0: [Prev: OFF] [Next: ON ]
  Position 1: [Prev: ON ] [Next: ON ]
  Position 2: [Prev: ON ] [Next: ON ]
  Position 3: [Prev: ON ] [Next: ON ]
  Position 4: [Prev: ON ] [Next: OFF]
```

## Activity Lifecycle & Data Sync

```
┌──────────────────┐
│   MainActivity   │
│                  │
│  onResume()      │ <── Reloads albums
│  onPause()       │ <── Saves albums
└────────┬─────────┘
         │ startActivity()
         ↓
┌──────────────────────┐
│ AlbumDetailActivity  │
│                      │
│  onResume()          │ <── Reloads albums (tags updated)
│  onPause()           │ <── Saves albums
└────────┬─────────────┘
         │ startActivity()
         ↓
┌──────────────────────┐
│ PhotoDetailActivity  │
│                      │
│  onResume()          │ <── No reload needed
│  onPause()           │ <── Saves albums (tags added/removed)
└──────────────────────┘
         │ finish()
         ↓
┌──────────────────────┐
│ AlbumDetailActivity  │
│                      │
│  onResume() fires!   │ <── Reloads albums (gets tag changes)
└──────────────────────┘
```

## Key Implementation Points

### 1. AlbumActivity Update
```java
// BEFORE (TODO implementation)
public void onPhotoClick(Photo photo, int position) {
    // TODO: Open photo detail view
    Toast.makeText(this, "Opening...", Toast.LENGTH_SHORT).show();
}

// AFTER (Complete implementation)
public void onPhotoClick(Photo photo, int position) {
    openPhotoDetail(position);
}

private void openPhotoDetail(int position) {
    Intent intent = new Intent(this, PhotoDetailActivity.class);
    intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_NAME, album.getName());
    intent.putExtra(AlbumDetailActivity.EXTRA_PHOTO_POSITION, position);
    startActivity(intent);
}
```

### 2. Data Refresh on Resume
```java
@Override
protected void onResume() {
    super.onResume();
    // Reload albums in case tags were modified
    albums = DataManager.loadAlbums(this);
    if (albumPosition >= 0 && albumPosition < albums.size()) {
        album = albums.get(albumPosition);
        adapter.setPhotos(album.getPhotos());
    }
}
```

### 3. Slideshow Navigation
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

## All Features Status: ✅ COMPLETE

- ✅ Open album from main screen
- ✅ Display photos in grid layout
- ✅ Add photos to album (FAB button)
- ✅ Remove photos from album (long press)
- ✅ Display photo full-screen
- ✅ Slideshow Previous button
- ✅ Slideshow Next button
- ✅ Position indicator (X / Y)
- ✅ Smart button enabling/disabling
- ✅ Tag management (bonus)
- ✅ Data persistence
- ✅ All navigation flows working

**Ready for testing! 🎉**
