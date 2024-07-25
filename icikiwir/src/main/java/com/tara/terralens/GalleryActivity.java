package com.tara.terralens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // Import for SwipeRefreshLayout

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private static final int RESULT_IMAGE_DELETED = 1001;
    private SwipeRefreshLayout swipeRefreshLayout; // Declaration for SwipeRefreshLayout
    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private List<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true); // Enable back button

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshGallery);

        recyclerView = findViewById(R.id.recycler_view_gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Initial load of image paths
        imagePaths = getAllShownImagesPath();

        adapter = new GalleryAdapter(imagePaths);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks
        if (item.getItemId() == android.R.id.home) {
            // Back button clicked
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<String> getAllShownImagesPath() {
        // Fetch image paths from the database
        ImageLocationDbHelper dbHelper = new ImageLocationDbHelper(this);
        List<String> imagePaths = dbHelper.getAllImagePaths();
        dbHelper.close();
        return imagePaths;
    }

    private void refreshGallery() {
        imagePaths.clear();
        imagePaths.addAll(getAllShownImagesPath());
        adapter.notifyDataSetChanged();

        swipeRefreshLayout.setRefreshing(false);
    }

    private void deleteImageFromGallery(String imagePath) {
        File imageFile = new File(imagePath);
        boolean isDeleted = deleteImage(imageFile);

        if (isDeleted) {
            // Notify the adapter that the item has been removed
            int position = imagePaths.indexOf(imagePath);
            if (position != -1) {
                imagePaths.remove(position);
                adapter.notifyItemRemoved(position);
            } else {
                Toast.makeText(this, "Failed to find image in the gallery", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deleteImage(File imageFile) {
        return imageFile.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_IMAGE_DELETED && resultCode == RESULT_OK && data != null) {
            String deletedImagePath = data.getStringExtra("deleted_image_path");
            deleteImageFromGallery(deletedImagePath);
        }
    }
}
