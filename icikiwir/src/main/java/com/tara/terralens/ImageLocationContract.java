package com.tara.terralens;

import android.provider.BaseColumns;

public final class ImageLocationContract {
    private ImageLocationContract() {}

    public static class ImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "images";
        public static final String COLUMN_NAME_URI = "uri";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_COUNTRY = "country";
    }
}

