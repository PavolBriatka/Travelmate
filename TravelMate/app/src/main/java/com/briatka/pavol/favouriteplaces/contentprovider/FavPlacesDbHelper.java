package com.briatka.pavol.favouriteplaces.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavPlacesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fav_places.db";

    private static final int VERSION = 4;


    FavPlacesDbHelper(Context context) {
        super(context, DATABASE_NAME,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE_PLACES = "CREATE TABLE IF NOT EXISTS " + FavPlacesContract.FavPlacesEntry.PLACES_TABLE_NAME + " (" +
                FavPlacesContract.FavPlacesEntry._ID + " INTEGER PRIMARY KEY, " +
                FavPlacesContract.FavPlacesEntry.PLACE_NAME + " TEXT, " +
                FavPlacesContract.FavPlacesEntry.PLACE_COORDINATES + " TEXT, " +
                FavPlacesContract.FavPlacesEntry.PLACE_PHOTO + " BLOB);";

        final String CREATE_TABLE_TRIPS = "CREATE TABLE IF NOT EXISTS " + FavPlacesContract.TripListsEntry.TRIPS_TABLE_NAME + " (" +
                FavPlacesContract.TripListsEntry._ID + " INTEGER PRIMARY KEY, " +
                FavPlacesContract.TripListsEntry.TRIP_NAME + " TEXT, " +
                FavPlacesContract.TripListsEntry.TRIP_LIST_JSON + " TEXT);";

        sqLiteDatabase.execSQL(CREATE_TABLE_PLACES);
        sqLiteDatabase.execSQL(CREATE_TABLE_TRIPS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavPlacesContract.FavPlacesEntry.PLACES_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavPlacesContract.TripListsEntry.TRIPS_TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
