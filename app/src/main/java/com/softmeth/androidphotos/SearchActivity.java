package com.softmeth.androidphotos;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softmeth.androidphotos.adapters.PhotoAdapter;
import com.softmeth.androidphotos.data.DataManager;
import com.softmeth.androidphotos.models.Album;
import com.softmeth.androidphotos.models.Photo;
import com.softmeth.androidphotos.models.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity implements PhotoAdapter.OnPhotoClickListener {

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private TextView emptyView;
    private Button btnAddCriteria;
    private Button btnSearch;
    private Button btnClear;
    private TextView criteriaDisplay;
    
    private List<Album> allAlbums;
    private List<SearchCriteria> searchCriteria;
    private boolean useConjunction = true; // true = AND, false = OR
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Search Photos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Load albums
        allAlbums = DataManager.loadAlbums(this);
        searchCriteria = new ArrayList<>();
        
        // Initialize views
        recyclerView = findViewById(R.id.search_results_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        btnAddCriteria = findViewById(R.id.btn_add_criteria);
        btnSearch = findViewById(R.id.btn_search);
        btnClear = findViewById(R.id.btn_clear);
        criteriaDisplay = findViewById(R.id.criteria_display);
        
        // Set up RecyclerView
        adapter = new PhotoAdapter(this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
        
        updateEmptyView();
        updateCriteriaDisplay();
        
        // Set up button listeners
        btnAddCriteria.setOnClickListener(v -> showAddCriteriaDialog());
        btnSearch.setOnClickListener(v -> performSearch());
        btnClear.setOnClickListener(v -> clearSearch());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showAddCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Search Criteria");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search_criteria, null);
        
        RadioGroup tagTypeGroup = dialogView.findViewById(R.id.tag_type_group);
        RadioButton rbPerson = dialogView.findViewById(R.id.rb_person);
        RadioButton rbLocation = dialogView.findViewById(R.id.rb_location);
        EditText etTagValue = dialogView.findViewById(R.id.et_tag_value);
        RadioGroup logicGroup = dialogView.findViewById(R.id.logic_group);
        RadioButton rbAnd = dialogView.findViewById(R.id.rb_and);
        RadioButton rbOr = dialogView.findViewById(R.id.rb_or);
        
        // Set default selections
        rbPerson.setChecked(true);
        if (searchCriteria.isEmpty()) {
            logicGroup.setVisibility(View.GONE);
        } else {
            rbAnd.setChecked(useConjunction);
            rbOr.setChecked(!useConjunction);
        }
        
        builder.setView(dialogView);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String tagType = rbPerson.isChecked() ? "person" : "location";
            String tagValue = etTagValue.getText().toString().trim();
            
            if (tagValue.isEmpty()) {
                Toast.makeText(this, "Tag value cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Update logic operator if not first criteria
            if (!searchCriteria.isEmpty()) {
                useConjunction = rbAnd.isChecked();
            }
            
            searchCriteria.add(new SearchCriteria(tagType, tagValue));
            updateCriteriaDisplay();
            Toast.makeText(this, "Criteria added", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void updateCriteriaDisplay() {
        if (searchCriteria.isEmpty()) {
            criteriaDisplay.setText("No search criteria. Tap 'Add Criteria' to begin.");
            criteriaDisplay.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        } else {
            StringBuilder sb = new StringBuilder("Search: ");
            for (int i = 0; i < searchCriteria.size(); i++) {
                SearchCriteria sc = searchCriteria.get(i);
                sb.append(sc.tagType).append(":\"").append(sc.tagValue).append("\"");
                if (i < searchCriteria.size() - 1) {
                    sb.append(useConjunction ? " AND " : " OR ");
                }
            }
            criteriaDisplay.setText(sb.toString());
            criteriaDisplay.setTextColor(getResources().getColor(android.R.color.black, null));
        }
    }
    
    private void performSearch() {
        if (searchCriteria.isEmpty()) {
            Toast.makeText(this, "Please add search criteria first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<Photo> results = new ArrayList<>();
        Set<Photo> uniquePhotos = new HashSet<>();
        
        // Search through all albums
        for (Album album : allAlbums) {
            for (Photo photo : album.getPhotos()) {
                if (matchesSearchCriteria(photo)) {
                    if (uniquePhotos.add(photo)) {
                        results.add(photo);
                    }
                }
            }
        }
        
        adapter.setPhotos(results);
        updateEmptyView();
        
        if (results.isEmpty()) {
            Toast.makeText(this, "No photos found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Found " + results.size() + " photo(s)", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean matchesSearchCriteria(Photo photo) {
        if (searchCriteria.isEmpty()) {
            return false;
        }
        
        if (useConjunction) {
            // AND logic - photo must match ALL criteria
            for (SearchCriteria sc : searchCriteria) {
                if (!hasMatchingTag(photo, sc.tagType, sc.tagValue)) {
                    return false;
                }
            }
            return true;
        } else {
            // OR logic - photo must match ANY criteria
            for (SearchCriteria sc : searchCriteria) {
                if (hasMatchingTag(photo, sc.tagType, sc.tagValue)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private boolean hasMatchingTag(Photo photo, String tagType, String tagValue) {
        for (Tag tag : photo.getTags()) {
            if (tag.getName().equalsIgnoreCase(tagType) && 
                tag.getValue().equalsIgnoreCase(tagValue)) {
                return true;
            }
        }
        return false;
    }
    
    private void clearSearch() {
        searchCriteria.clear();
        adapter.setPhotos(new ArrayList<>());
        updateCriteriaDisplay();
        updateEmptyView();
        Toast.makeText(this, "Search cleared", Toast.LENGTH_SHORT).show();
    }
    
    private void updateEmptyView() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onPhotoClick(Photo photo, int position) {
        // Find which album contains this photo
        String albumName = findAlbumForPhoto(photo);
        if (albumName != null) {
            // Find the position in that album
            Album album = findAlbumByName(albumName);
            if (album != null) {
                int photoPosition = album.getPhotos().indexOf(photo);
                if (photoPosition >= 0) {
                    android.content.Intent intent = new android.content.Intent(this, PhotoDetailActivity.class);
                    intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_NAME, albumName);
                    intent.putExtra(AlbumDetailActivity.EXTRA_PHOTO_POSITION, photoPosition);
                    startActivity(intent);
                }
            }
        }
    }
    
    @Override
    public void onPhotoLongClick(Photo photo, int position) {
        // Show photo info
        String albumName = findAlbumForPhoto(photo);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(photo.getFileName());
        builder.setMessage("Album: " + (albumName != null ? albumName : "Unknown"));
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private String findAlbumForPhoto(Photo photo) {
        for (Album album : allAlbums) {
            if (album.getPhotos().contains(photo)) {
                return album.getName();
            }
        }
        return null;
    }
    
    private Album findAlbumByName(String name) {
        for (Album album : allAlbums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }
        return null;
    }
    
    private static class SearchCriteria {
        String tagType;
        String tagValue;
        
        SearchCriteria(String tagType, String tagValue) {
            this.tagType = tagType;
            this.tagValue = tagValue;
        }
    }
}
