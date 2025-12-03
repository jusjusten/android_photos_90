package com.softmeth.androidphotos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.softmeth.androidphotos.R;
import com.softmeth.androidphotos.models.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying albums in a RecyclerView.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    
    private List<Album> albums;
    private OnAlbumClickListener listener;
    
    public interface OnAlbumClickListener {
        void onAlbumClick(Album album, int position);
        void onAlbumLongClick(Album album, int position);
    }
    
    public AlbumAdapter(OnAlbumClickListener listener) {
        this.albums = new ArrayList<>();
        this.listener = listener;
    }
    
    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }
    
    public List<Album> getAlbums() {
        return albums;
    }
    
    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.bind(album);
    }
    
    @Override
    public int getItemCount() {
        return albums.size();
    }
    
    class AlbumViewHolder extends RecyclerView.ViewHolder {
        private TextView albumName;
        private TextView photoCount;
        
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.album_name);
            photoCount = itemView.findViewById(R.id.album_photo_count);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAlbumClick(albums.get(position), position);
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAlbumLongClick(albums.get(position), position);
                    return true;
                }
                return false;
            });
        }
        
        public void bind(Album album) {
            albumName.setText(album.getName());
            int count = album.getPhotoCount();
            photoCount.setText(count + (count == 1 ? " photo" : " photos"));
        }
    }
}
