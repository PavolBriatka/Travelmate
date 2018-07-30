package com.briatka.pavol.favouriteplaces.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavPlacesContract {

    public static final String AUTHORITY = "com.briatka.pavol.favouriteplaces";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_PLACES = "favourite_places";
    public static final String PATH_TRIP_LISTS = "trip_lists";

    public static final class FavPlacesEntry implements BaseColumns {

        public static final Uri PLACES_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();

        public static final String PLACES_TABLE_NAME = "favourite_places";

        public static final String PLACE_NAME = "place_name";
        public static final String PLACE_COORDINATES = "place_coordinates";
        public static final String PLACE_PHOTO = "place_photo";
    }

    public static final class TripListsEntry implements BaseColumns {
        public static final Uri TRIPS_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIP_LISTS).build();

        public static final String TRIPS_TABLE_NAME = "trip_lists";

        public static final String TRIP_LIST_JSON = "trip_list_json_data";
        public static final String TRIP_NAME = "trip_name";
    }
}
