package com.tara.terralens;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.tara.terralens.ImageLocationDbHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CaptureActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;

    private ImageView imageView;
    private Bitmap captureImage;
    private String currentPhotoPath;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private double lastCapturedLatitude;
    private double lastCapturedLongitude;
    private String lastCapturedCity;
    private String lastCapturedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        imageView = findViewById(R.id.imageView);
        Button captureButton = findViewById(R.id.button_capture);
        Button returnButton = findViewById(R.id.button_return);
        Button locationInfoButton = findViewById(R.id.button_location_info);

        captureButton.setOnClickListener(v -> dispatchTakePictureIntent());
        returnButton.setOnClickListener(v -> finish());
        locationInfoButton.setOnClickListener(v -> showLastLocationInformation());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            checkLocationPermissionAndRequestLocation();
        }
    }

    private void checkLocationPermissionAndRequestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle the case where permissions are not granted
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    lastCapturedLatitude = location.getLatitude();
                    lastCapturedLongitude = location.getLongitude();
                    captureImageWithLocation(location);
                    stopLocationUpdates();
                }
            }
        };
    }

    private void captureImageWithLocation(Location location) {
        // Create an image file
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            // Assign the path of the created file to currentPhotoPath
            currentPhotoPath = photoFile.getAbsolutePath();

            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.tara.terralens.fileprovider",
                    photoFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                // Rotate the bitmap by +90 degrees
                Matrix matrix = new Matrix();
                matrix.postRotate(+90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

                imageView.setImageBitmap(rotatedBitmap);

                // Show location information dialog after photo is taken and saved
                Location location = new Location("");
                location.setLatitude(lastCapturedLatitude);
                location.setLongitude(lastCapturedLongitude);
                showLocationInformationDialog(location, currentPhotoPath);

                // Notify the user that the image was saved successfully
                new AlertDialog.Builder(this)
                        .setTitle("Gambar Disimpan")
                        .setMessage("Gambar berhasil disimpan ke penyimpanan internal.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissionAndRequestLocation();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLocationInformationDialog(Location location, String imagePath) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressString = address.getAddressLine(0);
                String city = address.getLocality();
                String country = address.getCountryName();
                lastCapturedCity = city;
                lastCapturedCountry = country;

                // Save the image and location information to the database
                saveImageToDatabase(imagePath, location.getLatitude(), location.getLongitude(), addressString, city, country);

                // Show a dialog indicating that the image and data were saved successfully
                new AlertDialog.Builder(this)
                        .setTitle("Location Information")
                        .setMessage("Latitude: " + location.getLatitude() + "\n" +
                                "Longitude: " + location.getLongitude() + "\n" +
                                "Address: " + addressString + "\n" +
                                "City: " + city + "\n" +
                                "Country: " + country)
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Show a toast to notify the user that data has been saved
                            Toast.makeText(this, "Data added to database", Toast.LENGTH_SHORT).show();
                        })
                        .show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImageToDatabase(String imagePath, double latitude, double longitude, String address, String city, String country) {
        SQLiteDatabase db = new ImageLocationDbHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_URI, imagePath);
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_LONGITUDE, longitude);
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_ADDRESS, address);
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_CITY, city);
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_COUNTRY, country);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ImageLocationContract.ImageEntry.TABLE_NAME, null, values);
        db.close();
    }

    private void showLastLocationInformation() {
        if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
            SQLiteDatabase db = new ImageLocationDbHelper(this).getReadableDatabase();

            String[] projection = {
                    ImageLocationContract.ImageEntry.COLUMN_NAME_URI,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_LATITUDE,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_LONGITUDE,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_ADDRESS,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_CITY,
                    ImageLocationContract.ImageEntry.COLUMN_NAME_COUNTRY
            };

            String selection = ImageLocationContract.ImageEntry.COLUMN_NAME_URI + " = ?";
            String[] selectionArgs = { currentPhotoPath };

            Cursor cursor = db.query(
                    ImageLocationContract.ImageEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_LONGITUDE));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_ADDRESS));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_CITY));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_COUNTRY));

                new AlertDialog.Builder(this)
                        .setTitle("Location Information")
                        .setMessage("Latitude: " + latitude + "\n" +
                                "Longitude: " + longitude + "\n" +
                                "Address: " + address + "\n" +
                                "City: " + city + "\n" +
                                "Country: " + country)
                        .setPositiveButton("OK", null)
                        .show();

                cursor.close();
            } else {
                Toast.makeText(this, "No location information available", Toast.LENGTH_SHORT).show();
            }

            db.close();
        } else {
            Toast.makeText(this, "No photo taken yet", Toast.LENGTH_SHORT).show();
        }
    }
}
