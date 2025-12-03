package com.softmeth.androidphotos.data;

import android.content.Context;
import com.softmeth.androidphotos.models.Album;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages saving and loading album data using serialization.
 */
public class DataManager {
    private static final String ALBUMS_FILE = "albums.dat";
    
    /**
     * Saves the list of albums to internal storage.
     */
    public static boolean saveAlbums(Context context, List<Album> albums) {
        try (FileOutputStream fos = context.openFileOutput(ALBUMS_FILE, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(albums);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Loads the list of albums from internal storage.
     * Returns an empty list if no data exists or an error occurs.
     */
    @SuppressWarnings("unchecked")
    public static List<Album> loadAlbums(Context context) {
        try (FileInputStream fis = context.openFileInput(ALBUMS_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Album>) ois.readObject();
        } catch (Exception e) {
            // File doesn't exist or error occurred - return empty list
            return new ArrayList<>();
        }
    }
}
