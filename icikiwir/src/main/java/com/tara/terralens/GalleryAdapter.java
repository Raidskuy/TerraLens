package com.tara.terralens;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tara.terralens.ImageDetailActivity;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private List<String> imagePaths;

    public GalleryAdapter(List<String> imagePaths){
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from((parent.getContext()))
                .inflate(R.layout.item_image_gallery, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);
        Glide.with(holder.itemView.getContext())
                .load(imagePath)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            // Open detail image activity
            Intent intent = new Intent(holder.itemView.getContext(), ImageDetailActivity.class);
            intent.putExtra("imagePath", imagePath);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount(){
        return imagePaths.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public GalleryViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_gallery_item);
        }
    }
}
