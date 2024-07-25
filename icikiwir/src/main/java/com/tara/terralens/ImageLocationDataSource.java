package com.tara.terralens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ImageLocationDataSource {
    private SQLiteDatabase database;
    private ImageLocationDbHelper dbHelper;

    public ImageLocationDataSource(Context context) {
        dbHelper = new ImageLocationDbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertImageLocation(ImageLocation imageLocation) {
        ContentValues values = new ContentValues();
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_URI, imageLocation.getUri());
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_LATITUDE, imageLocation.getLatitude());
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_LONGITUDE, imageLocation.getLongitude());
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_ADDRESS, imageLocation.getAddress());
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_CITY, imageLocation.getCity());
        values.put(ImageLocationContract.ImageEntry.COLUMN_NAME_COUNTRY, imageLocation.getCountry());

        return database.insert(ImageLocationContract.ImageEntry.TABLE_NAME, null, values);
    }

    public List<ImageLocation> getAllImageLocations() {
        List<ImageLocation> imageLocations = new ArrayList<>();
        Cursor cursor = database.query(
                ImageLocationContract.ImageEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ImageLocation imageLocation = new ImageLocation();
                imageLocation.setUri(cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_URI)));
                imageLocation.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_LATITUDE)));
                imageLocation.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_LONGITUDE)));
                imageLocation.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_ADDRESS)));
                imageLocation.setCity(cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_CITY)));
                imageLocation.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_COUNTRY)));

                imageLocations.add(imageLocation);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return imageLocations;
    }
}
