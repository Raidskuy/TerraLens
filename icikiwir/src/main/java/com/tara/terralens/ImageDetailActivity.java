package com.tara.terralens;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

public class ImageDetailActivity extends AppCompatActivity {

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        // Get image path from intent
        imagePath = getIntent().getStringExtra("imagePath");

        // Load image into ImageView using Glide
        ImageView imageView = findViewById(R.id.image_view_detail);
        Glide.with(this)
                .load(imagePath)
                .into(imageView);

        // Set up OK button
        Button okButton = findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close ImageDetailActivity and return to GalleryActivity
                finish();
            }
        });

        // Set up Delete button
        Button deleteButton = findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show confirmation dialog before deleting image
                showDeleteConfirmationDialog();
            }
        });

        // Set up Location button
        Button locationButton = findViewById(R.id.button_location);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve and display location information
                showLocationInformationDialog(imagePath);
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the image from the database and storage
                    deleteImage();
                    // Close ImageDetailActivity and return to GalleryActivity
                    finish();
                })
                .setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void deleteImage() {
        // Delete image entry from the database
        ImageLocationDbHelper dbHelper = new ImageLocationDbHelper(this);
        dbHelper.deleteImageEntry(imagePath);

        // Delete the image file from storage
        File imageFile = new File(imagePath);
        boolean isDeleted = deleteImageFile(imageFile);

        if (isDeleted) {
            // Set result indicating successful deletion
            setResult(RESULT_OK, new Intent().putExtra("deleted_image_path", imagePath));
        } else {
            // Set result indicating deletion failure
            setResult(RESULT_CANCELED);
        }

        // Finish the activity
        finish();
    }

    private boolean deleteImageFile(File imageFile) {
        if (imageFile.exists()) {
            return imageFile.delete();
        }
        return false;
    }

    private void showLocationInformationDialog(String imagePath) {
        // Log the image path to see if it's correctly received
        Log.d("ImageDetailActivity", "Image Path: " + imagePath);

        // Check if the image path is not null or empty
        if (imagePath != null && !imagePath.isEmpty()) {
            // Get readable database
            SQLiteDatabase db = new ImageLocationDbHelper(this).getReadableDatabase();

            // Define a projection that specifies only latitude, longitude, address, city, and country columns from the database
            String[] projection = {
                    ImageLocationContract.ImageEntry.COLUMN_NAME_LATITUDE,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_LONGITUDE,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_ADDRESS,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_CITY,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_COUNTRY
            };

            // Filter results WHERE "imageUri" = 'imageUri'
            String selection = ImageLocationContract.ImageEntry.COLUMN_NAME_URI + " = '" + imagePath + "'";

            Cursor cursor = db.query(
                    ImageLocationContract.ImageEntry.TABLE_NAME,   // The table to query
                    projection,                                     // The array of columns to return
                    selection,                                      // The columns for the WHERE clause
                    null,                                           // No values for the WHERE clause
                    null,                                           // Don't group the rows
                    null,                                           // Don't filter by row groups
                    null                                            // The sort order
            );

            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve location information from the cursor
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_LONGITUDE));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_ADDRESS));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_CITY));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_COUNTRY));

                // Format location information and display it
                String locationInfo = "Latitude: " + latitude + "\n" +
                        "Longitude: " + longitude + "\n" +
                        "Address: " + address + "\n" +
                        "City: " + city + "\n" +
                        "Country: " + country;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Information");
                builder.setMessage(locationInfo);
                builder.setPositiveButton("OK", null);
                builder.show();
            } else {
                // If cursor is null or empty, log a message
                Log.e("ImageDetailActivity", "Cursor is null or empty");
            }

            // Close the cursor and the database
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        } else {
            // If image path is null or empty, log a message
            Log.e("ImageDetailActivity", "Image Path is null or empty");
        }
    }
}
