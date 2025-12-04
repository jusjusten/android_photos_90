package com.softmeth.androidphotos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softmeth.androidphotos.adapters.PhotoAdapter;
import com.softmeth.androidphotos.data.DataManager;
import com.softmeth.androidphotos.models.Album;
import com.softmeth.androidphotos.models.Photo;

import java.util.List;

public class AlbumActivity extends AppCompatActivity implements PhotoAdapter.OnPhotoClickListener {

    public static final String EXTRA_ALBUM_NAME = "album_name";
    public static final String EXTRA_ALBUM_POSITION = "album_position";

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private TextView emptyView;
    private Album album;
    private int albumPosition;
    private List<Album> albums;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get info from intent
        String albumName = getIntent().getStringExtra(EXTRA_ALBUM_NAME);
        albumPosition = getIntent().getIntExtra(EXTRA_ALBUM_POSITION, -1);

        // Load albums
        albums = DataManager.loadAlbums(this);
        if (albumPosition >= 0 && albumPosition < albums.size()) {
            album = albums.get(albumPosition);
        } else {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(album.getName());
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.photos_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        FloatingActionButton fabAddPhoto = findViewById(R.id.fab_add_photo);

        // Set up RecyclerView with grid layout
        adapter = new PhotoAdapter(this, this);
        adapter.setPhotos(album.getPhotos());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns
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

        // Set up FAB click listener
        fabAddPhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save albums when leaving
        DataManager.saveAlbums(this, albums);
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
        String fileName = getFileNameFromUri(uri);

        // Create photo object
        Photo photo = new Photo(uri, fileName);

        // Add to album
        if (album.addPhoto(photo)) {
            adapter.setPhotos(album.getPhotos());
            updateEmptyView();
            DataManager.saveAlbums(this, albums);
            Toast.makeText(this, "Photo added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Photo already in album", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int index = path.lastIndexOf('/');
            if (index != -1) {
                return path.substring(index + 1);
            }
        }
        return "photo_" + System.currentTimeMillis();
    }

    @Override
    public void onPhotoClick(Photo photo, int position) {
        // TODO: Open photo detail view
        Toast.makeText(this, "Opening " + photo.getFileName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPhotoLongClick(Photo photo, int position) {
        showPhotoOptionsDialog(photo, position);
    }

    private void showPhotoOptionsDialog(Photo photo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(photo.getFileName());
        String[] options = {"View", "Delete"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // View
                    onPhotoClick(photo, position);
                    break;
                case 1: // Delete
                    showDeletePhotoConfirmation(photo, position);
                    break;
            }
        });

        builder.show();
    }

    private void showDeletePhotoConfirmation(Photo photo, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Photo");
        builder.setMessage("Delete " + photo.getFileName() + "?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            album.removePhoto(photo);
            adapter.setPhotos(album.getPhotos());
            updateEmptyView();
            DataManager.saveAlbums(this, albums);
            Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}