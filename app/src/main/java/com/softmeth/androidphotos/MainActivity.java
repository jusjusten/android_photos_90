package com.softmeth.androidphotos;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softmeth.androidphotos.adapters.AlbumAdapter;
import com.softmeth.androidphotos.data.DataManager;
import com.softmeth.androidphotos.models.Album;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnAlbumClickListener {

    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private TextView emptyView;
    private List<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.albums_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        FloatingActionButton fabAddAlbum = findViewById(R.id.fab_add_album);

        // Load albums from storage
        albums = DataManager.loadAlbums(this);

        // Set up RecyclerView
        adapter = new AlbumAdapter(this);
        adapter.setAlbums(albums);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Update empty view visibility
        updateEmptyView();

        // Set up FAB click listener
        fabAddAlbum.setOnClickListener(v -> showCreateAlbumDialog());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save albums when app goes to background
        DataManager.saveAlbums(this, albums);
    }

    private void updateEmptyView() {
        if (albums.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showCreateAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Album");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Album name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String albumName = input.getText().toString().trim();
            if (albumName.isEmpty()) {
                Toast.makeText(this, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if album already exists
            for (Album album : albums) {
                if (album.getName().equalsIgnoreCase(albumName)) {
                    Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Create new album
            Album newAlbum = new Album(albumName);
            albums.add(newAlbum);
            adapter.notifyItemInserted(albums.size() - 1);
            updateEmptyView();
            DataManager.saveAlbums(this, albums);
            Toast.makeText(this, "Album created", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showRenameAlbumDialog(Album album, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Album");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(album.getName());
        input.setSelection(album.getName().length());
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if album name already exists
            for (Album a : albums) {
                if (a != album && a.getName().equalsIgnoreCase(newName)) {
                    Toast.makeText(this, "Album name already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            album.setName(newName);
            adapter.notifyItemChanged(position);
            DataManager.saveAlbums(this, albums);
            Toast.makeText(this, "Album renamed", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showAlbumOptionsDialog(Album album, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(album.getName());
        String[] options = {"Open", "Rename", "Delete"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Open
                    openAlbum(album);
                    break;
                case 1: // Rename
                    showRenameAlbumDialog(album, position);
                    break;
                case 2: // Delete
                    showDeleteConfirmation(album, position);
                    break;
            }
        });

        builder.show();
    }

    private void showDeleteConfirmation(Album album, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Album");
        builder.setMessage("Are you sure you want to delete \"" + album.getName() + "\"?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            albums.remove(position);
            adapter.notifyItemRemoved(position);
            updateEmptyView();
            DataManager.saveAlbums(this, albums);
            Toast.makeText(this, "Album deleted", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openAlbum(Album album) {
        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_NAME, album.getName());
        startActivity(intent);
    }

    @Override
    public void onAlbumClick(Album album, int position) {
        openAlbum(album);
    }

    @Override
    public void onAlbumLongClick(Album album, int position) {
        showAlbumOptionsDialog(album, position);
    }
}
