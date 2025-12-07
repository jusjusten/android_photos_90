package com.softmeth.androidphotos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softmeth.androidphotos.adapters.PhotoAdapter;
import com.softmeth.androidphotos.data.DataManager;
import com.softmeth.androidphotos.models.Album;
import com.softmeth.androidphotos.models.Photo;

import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity implements PhotoAdapter.OnPhotoClickListener {

    public static final String EXTRA_ALBUM_NAME = "album_name";
    public static final String EXTRA_PHOTO_POSITION = "photo_position";

    private Album album;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private TextView emptyView;
    private List<Album> allAlbums;
    
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        // Get album name from intent
        String albumName = getIntent().getStringExtra(EXTRA_ALBUM_NAME);
        if (albumName == null) {
            finish();
            return;
        }

        // Load all albums and find this album
        allAlbums = DataManager.loadAlbums(this);
        album = findAlbumByName(albumName);
        if (album == null) {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(album.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        recyclerView = findViewById(R.id.photos_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        FloatingActionButton fabAddPhoto = findViewById(R.id.fab_add_photo);

        // Set up RecyclerView with grid layout (3 columns)
        adapter = new PhotoAdapter(this, this);
        adapter.setPhotos(album.getPhotos());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        updateEmptyView();

        // Set up photo picker
        pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    addPhotoToAlbum(uri);
                }
            }
        );

        // Set up FAB
        fabAddPhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save albums when leaving
        DataManager.saveAlbums(this, allAlbums);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Album findAlbumByName(String name) {
        for (Album a : allAlbums) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    private void updateEmptyView() {
        if (album.getPhotos().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void addPhotoToAlbum(Uri uri) {
        // Get filename from URI
        String fileName = uri.getLastPathSegment();
        if (fileName == null) {
            fileName = "Photo_" + System.currentTimeMillis();
        }

        // Take persistable URI permission
        try {
            getContentResolver().takePersistableUriPermission(uri, 
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException e) {
            // If we can't get persistable permission, the URI might still work
        }

        Photo photo = new Photo(uri, fileName);
        if (album.addPhoto(photo)) {
            adapter.setPhotos(album.getPhotos());
            updateEmptyView();
            DataManager.saveAlbums(this, allAlbums);
            Toast.makeText(this, "Photo added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Photo already in album", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPhotoOptionsDialog(Photo photo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(photo.getFileName());
        String[] options = {"View", "Move to Album", "Remove"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // View
                    openPhoto(position);
                    break;
                case 1: // Move
                    showMovePhotoDialog(photo, position);
                    break;
                case 2: // Remove
                    showRemovePhotoConfirmation(photo, position);
                    break;
            }
        });

        builder.show();
    }

    private void showMovePhotoDialog(Photo photo, int position) {
        // Get list of other albums
        List<Album> otherAlbums = new java.util.ArrayList<>();
        for (Album a : allAlbums) {
            if (!a.equals(album)) {
                otherAlbums.add(a);
            }
        }

        if (otherAlbums.isEmpty()) {
            Toast.makeText(this, "No other albums available", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] albumNames = new String[otherAlbums.size()];
        for (int i = 0; i < otherAlbums.size(); i++) {
            albumNames[i] = otherAlbums.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Move to Album");
        builder.setItems(albumNames, (dialog, which) -> {
            Album targetAlbum = otherAlbums.get(which);
            
            // Remove from current album
            album.getPhotos().remove(position);
            adapter.notifyItemRemoved(position);
            
            // Add to target album
            targetAlbum.addPhoto(photo);
            
            updateEmptyView();
            DataManager.saveAlbums(this, allAlbums);
            Toast.makeText(this, "Photo moved to " + targetAlbum.getName(), Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showRemovePhotoConfirmation(Photo photo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Photo");
        builder.setMessage("Remove \"" + photo.getFileName() + "\" from this album?");

        builder.setPositiveButton("Remove", (dialog, which) -> {
            album.getPhotos().remove(position);
            adapter.notifyItemRemoved(position);
            updateEmptyView();
            DataManager.saveAlbums(this, allAlbums);
            Toast.makeText(this, "Photo removed", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void openPhoto(int position) {
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(EXTRA_ALBUM_NAME, album.getName());
        intent.putExtra(EXTRA_PHOTO_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onPhotoClick(Photo photo, int position) {
        openPhoto(position);
    }

    @Override
    public void onPhotoLongClick(Photo photo, int position) {
        showPhotoOptionsDialog(photo, position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload albums in case tags were modified
        allAlbums = DataManager.loadAlbums(this);
        album = findAlbumByName(album.getName());
        if (album != null) {
            adapter.setPhotos(album.getPhotos());
            adapter.notifyDataSetChanged();
        }
    }
}
