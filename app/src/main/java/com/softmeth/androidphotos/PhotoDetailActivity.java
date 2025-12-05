package com.softmeth.androidphotos;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.softmeth.androidphotos.data.DataManager;
import com.softmeth.androidphotos.models.Album;
import com.softmeth.androidphotos.models.Photo;
import com.softmeth.androidphotos.models.Tag;

import java.util.List;

public class PhotoDetailActivity extends AppCompatActivity {

    private Album album;
    private List<Album> allAlbums;
    private int currentPosition;
    private ImageView imageView;
    private TextView fileNameView;
    private LinearLayout tagsContainer;
    private Button btnPrevious;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        String albumName = getIntent().getStringExtra(AlbumDetailActivity.EXTRA_ALBUM_NAME);
        currentPosition = getIntent().getIntExtra(AlbumDetailActivity.EXTRA_PHOTO_POSITION, 0);

        if (albumName == null) {
            finish();
            return;
        }

        allAlbums = DataManager.loadAlbums(this);
        album = findAlbumByName(albumName);
        if (album == null || album.getPhotos().isEmpty()) {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(album.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageView = findViewById(R.id.photo_image_view);
        fileNameView = findViewById(R.id.photo_file_name);
        tagsContainer = findViewById(R.id.tags_container);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);

        btnPrevious.setOnClickListener(v -> showPreviousPhoto());
        btnNext.setOnClickListener(v -> showNextPhoto());

        displayCurrentPhoto();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataManager.saveAlbums(this, allAlbums);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_add_tag) {
            showAddTagDialog();
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

    private void displayCurrentPhoto() {
        if (currentPosition < 0 || currentPosition >= album.getPhotos().size()) {
            return;
        }

        Photo photo = album.getPhotos().get(currentPosition);
        
        // Display image
        imageView.setImageURI(photo.getUri());
        
        // Display filename
        fileNameView.setText(photo.getFileName());
        
        // Update navigation buttons
        btnPrevious.setEnabled(currentPosition > 0);
        btnNext.setEnabled(currentPosition < album.getPhotos().size() - 1);
        
        // Display tags
        displayTags(photo);
        
        // Update subtitle
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle((currentPosition + 1) + " / " + album.getPhotos().size());
        }
    }

    private void displayTags(Photo photo) {
        tagsContainer.removeAllViews();
        
        List<Tag> tags = photo.getTags();
        if (tags.isEmpty()) {
            TextView noTagsView = new TextView(this);
            noTagsView.setText("No tags");
            noTagsView.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
            tagsContainer.addView(noTagsView);
        } else {
            for (Tag tag : tags) {
                View tagView = getLayoutInflater().inflate(R.layout.item_tag, tagsContainer, false);
                TextView tagText = tagView.findViewById(R.id.tag_text);
                tagText.setText(tag.getName() + ": " + tag.getValue());
                
                tagView.setOnClickListener(v -> showDeleteTagDialog(photo, tag));
                tagsContainer.addView(tagView);
            }
        }
    }

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

    private void showAddTagDialog() {
        Photo photo = album.getPhotos().get(currentPosition);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Tag");
        
        // Create custom layout for dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        // Tag type selection (Person or Location only)
        final String[] tagTypes = {"person", "location"};
        final int[] selectedType = {0};
        
        TextView typeLabel = new TextView(this);
        typeLabel.setText("Tag Type:");
        layout.addView(typeLabel);
        
        Button typeButton = new Button(this);
        typeButton.setText(tagTypes[0]);
        typeButton.setOnClickListener(v -> {
            AlertDialog.Builder typeBuilder = new AlertDialog.Builder(this);
            typeBuilder.setTitle("Select Tag Type");
            typeBuilder.setItems(tagTypes, (dialog, which) -> {
                selectedType[0] = which;
                typeButton.setText(tagTypes[which]);
            });
            typeBuilder.show();
        });
        layout.addView(typeButton);
        
        // Tag value input
        TextView valueLabel = new TextView(this);
        valueLabel.setText("Tag Value:");
        valueLabel.setPadding(0, 20, 0, 0);
        layout.addView(valueLabel);
        
        final EditText valueInput = new EditText(this);
        valueInput.setInputType(InputType.TYPE_CLASS_TEXT);
        valueInput.setHint("Enter tag value");
        layout.addView(valueInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String tagValue = valueInput.getText().toString().trim();
            if (tagValue.isEmpty()) {
                Toast.makeText(this, "Tag value cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String tagName = tagTypes[selectedType[0]];
            if (photo.addTag(tagName, tagValue)) {
                displayTags(photo);
                DataManager.saveAlbums(this, allAlbums);
                Toast.makeText(this, "Tag added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tag already exists", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteTagDialog(Photo photo, Tag tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Tag");
        builder.setMessage("Delete tag \"" + tag.getName() + ": " + tag.getValue() + "\"?");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (photo.removeTag(tag.getName(), tag.getValue())) {
                displayTags(photo);
                DataManager.saveAlbums(this, allAlbums);
                Toast.makeText(this, "Tag deleted", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
