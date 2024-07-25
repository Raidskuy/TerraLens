package com.tara.terralens;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ImageLocationDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "image_locations.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ImageLocationContract.ImageEntry.TABLE_NAME + " (" +
                    ImageLocationContract.ImageEntry._ID + " INTEGER PRIMARY KEY," +
                    ImageLocationContract.ImageEntry.COLUMN_NAME_URI + " TEXT," +
                    ImageLocationContract.ImageEntry.COLUMN_NAME_LATITUDE + " REAL," +
                    ImageLocationContract.ImageEntry.COLUMN_NAME_LONGITUDE + " REAL," +
                    ImageLocationContract.ImageEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    ImageLocationContract.ImageEntry.COLUMN_NAME_CITY + " TEXT," +
                    ImageLocationContract.ImageEntry.COLUMN_NAME_COUNTRY + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ImageLocationContract.ImageEntry.TABLE_NAME;

    public ImageLocationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public List<String> getAllImagePaths() {
        List<String> imagePaths = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ImageLocationContract.ImageEntry.COLUMN_NAME_URI
        };

        Cursor cursor = db.query(
                ImageLocationContract.ImageEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,                   // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null                    // don't sort
        );

        while (cursor.moveToNext()) {
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(ImageLocationContract.ImageEntry.COLUMN_NAME_URI));
            imagePaths.add(imagePath);
        }

        cursor.close();
        return imagePaths;
    }

    public void deleteImageEntry(String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = ImageLocationContract.ImageEntry.COLUMN_NAME_URI + " = ?";
        String[] selectionArgs = {imagePath};
        db.delete(ImageLocationContract.ImageEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

}
