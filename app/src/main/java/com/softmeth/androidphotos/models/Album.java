package com.softmeth.photosandroid.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a photo album.
 * Simplified version without dates.
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private List<Photo> photos;
    
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    public boolean addPhoto(Photo photo) {
        if (photos.contains(photo)) {
            return false;
        }
        photos.add(photo);
        return true;
    }
    
    public boolean removePhoto(Photo photo) {
        return photos.remove(photo);
    }
    
    public List<Photo> getPhotos() {
        return new ArrayList<>(photos);
    }
    
    public int getPhotoCount() {
        return photos.size();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Album album = (Album) obj;
        return name.equalsIgnoreCase(album.name);
    }
    
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
    
    @Override
    public String toString() {
        return name + " (" + getPhotoCount() + " photos)";
    }
}
