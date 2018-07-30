package com.briatka.pavol.favouriteplaces.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract.FavPlacesEntry.PLACES_TABLE_NAME;
import static com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract.TripListsEntry.TRIPS_TABLE_NAME;

public class FPContentProvider extends ContentProvider {

    public static final int FAVOURITE_PLACES = 100;
    public static final int FAVOURITE_PLACES_ROW = 101;
    public static final int TRIP_LISTS = 200;
    public static final int TRIP_LISTS_ROW = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavPlacesContract.AUTHORITY, FavPlacesContract.PATH_PLACES, FAVOURITE_PLACES);
        uriMatcher.addURI(FavPlacesContract.AUTHORITY, FavPlacesContract.PATH_PLACES + "/#", FAVOURITE_PLACES_ROW);
        uriMatcher.addURI(FavPlacesContract.AUTHORITY, FavPlacesContract.PATH_TRIP_LISTS, TRIP_LISTS);
        uriMatcher.addURI(FavPlacesContract.AUTHORITY, FavPlacesContract.PATH_TRIP_LISTS + "/#", TRIP_LISTS_ROW);

        return uriMatcher;
    }

    private FavPlacesDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new FavPlacesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVOURITE_PLACES:
                long placesId = database.insert(PLACES_TABLE_NAME, null, contentValues);
                if (placesId > 0) {
                    returnUri = ContentUris.withAppendedId(FavPlacesContract.FavPlacesEntry.PLACES_CONTENT_URI, placesId);
                } else {
                    throw new android.database.SQLException("Failed to insert row");
                }
                break;
            case TRIP_LISTS:
                long tripsId = database.insert(TRIPS_TABLE_NAME, null, contentValues);
                if (tripsId > 0) {
                    returnUri = ContentUris.withAppendedId(FavPlacesContract.TripListsEntry.TRIPS_CONTENT_URI, tripsId);
                } else {
                    throw new android.database.SQLException("Failed to insert row");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase database = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case FAVOURITE_PLACES:
                returnCursor = database.query(PLACES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVOURITE_PLACES_ROW:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                returnCursor = database.query(PLACES_TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRIP_LISTS:
                returnCursor = database.query(TRIPS_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRIP_LISTS_ROW:
                String tId = uri.getPathSegments().get(1);
                String tSelection = "_id=?";
                String[] tSelectionArgs = new String[]{tId};

                returnCursor = database.query(TRIPS_TABLE_NAME,
                        projection,
                        tSelection,
                        tSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deletedRows;

        switch (match) {
            case FAVOURITE_PLACES_ROW:
                String pId = uri.getPathSegments().get(1);
                String pWhereClause = "_id=?";
                String[] pWhereArgs = new String[]{pId};
                deletedRows = database.delete(PLACES_TABLE_NAME,
                        pWhereClause,
                        pWhereArgs);
                break;
            case TRIP_LISTS_ROW:
                String tId = uri.getPathSegments().get(1);
                String tWhereClause = "_id=?";
                String[] tWhereArgs = new String[]{tId};
                deletedRows = database.delete(TRIPS_TABLE_NAME,
                        tWhereClause,
                        tWhereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int updatedRows;

        switch (match) {
            case TRIP_LISTS_ROW:
                String idPathSegment = uri.getPathSegments().get(1);
                String selection = "_id=?";
                String[] selectionArgs = new String[]{idPathSegment};

                updatedRows = database.update(TRIPS_TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }
}
