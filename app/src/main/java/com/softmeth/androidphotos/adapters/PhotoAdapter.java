package com.softmeth.androidphotos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softmeth.androidphotos.R;
import com.softmeth.androidphotos.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<Photo> photos;
    private OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo, int position);
        void onPhotoLongClick(Photo photo, int position);
    }

    public PhotoAdapter(Context context, OnPhotoClickListener listener) {
        this.context = context;
        this.photos = new ArrayList<>();
        this.listener = listener;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.bind(photo, position);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView photoNameText;

        PhotoViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            photoNameText = itemView.findViewById(R.id.photoNameText);
        }

        void bind(Photo photo, int position) {
            // Set photo name
            photoNameText.setText(photo.getFileName());

            // Load image from URI
            try {
                photoImageView.setImageURI(photo.getUri());
            } catch (Exception e) {
                // If image fails to load, use a placeholder
                photoImageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPhotoClick(photo, position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onPhotoLongClick(photo, position);
                }
                return true;
            });
        }
    }
}