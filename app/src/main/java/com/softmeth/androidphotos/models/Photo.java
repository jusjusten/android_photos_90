package com.softmeth.androidphotos.models;

import android.net.Uri;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a photo.
 * Simplified - no dates, filename is caption.
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String uriString;
    private String fileName;
    private List<Tag> tags;
    
    public Photo(Uri uri, String fileName) {
        this.uriString = uri.toString();
        this.fileName = fileName;
        this.tags = new ArrayList<>();
    }
    
    public Uri getUri() {
        return Uri.parse(uriString);
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public boolean addTag(String tagName, String tagValue) {
        Tag newTag = new Tag(tagName, tagValue);
        if (tags.contains(newTag)) {
            return false;
        }
        tags.add(newTag);
        return true;
    }
    
    public boolean removeTag(String tagName, String tagValue) {
        Tag tagToRemove = new Tag(tagName, tagValue);
        return tags.remove(tagToRemove);
    }
    
    public boolean hasTag(String tagName, String tagValue) {
        return tags.contains(new Tag(tagName, tagValue));
    }
    
    public List<Tag> getTags() {
        return new ArrayList<>(tags);
    }
    
    public List<Tag> getTagsByName(String tagName) {
        List<Tag> result = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                result.add(tag);
            }
        }
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Photo photo = (Photo) obj;
        return uriString.equals(photo.uriString);
    }
    
    @Override
    public int hashCode() {
        return uriString.hashCode();
    }
    
    @Override
    public String toString() {
        return fileName;
    }
}
```

Now you should have:
```
com.softmeth.photosandroid/
├── MainActivity.java
└── models/
    ├── Album.java ✅
    ├── Photo.java ✅
    └── Tag.java ✅
