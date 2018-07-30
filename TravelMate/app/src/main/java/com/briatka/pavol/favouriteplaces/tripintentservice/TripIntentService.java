package com.briatka.pavol.favouriteplaces.tripintentservice;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract;
import com.briatka.pavol.favouriteplaces.customobjects.CustomPlace;
import com.briatka.pavol.favouriteplaces.widget.TripWidgetProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TripIntentService extends IntentService {

    public static final String ACTION_DESTINATION_VISIT =
            "com.briatka.pavol.favouriteplaces.tripintentservice.action.destination_visit";
    public static final String TRIP_ID_KEY = "trip_id";
    public static final String DESTINATION_POSITION_KEY = "destination_position";
    public static final String TRIP_DATA_KEY = "trip_data";

    public TripIntentService() {
        super("TripIntentService");
    }

    public static void startActionDestinationVisit(Context context) {
        Intent intent = new Intent(context, TripIntentService.class);
        intent.setAction(ACTION_DESTINATION_VISIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String tripId = intent.getStringExtra(TRIP_ID_KEY);
            int destinationIndex = intent.getIntExtra(DESTINATION_POSITION_KEY, 0);
            String tripListData = intent.getStringExtra(TRIP_DATA_KEY);
            if (ACTION_DESTINATION_VISIT.equals(action)) {
                handleActionDestinationVisit(tripId, destinationIndex, tripListData);
            }
        }
    }

    private void handleActionDestinationVisit(String tripDbId, int destinationArrayIndex, String dataList) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CustomPlace>>() {
        }.getType();
        ArrayList<CustomPlace> destinationArray = gson.fromJson(dataList, type);
        Boolean visited;

        CustomPlace clickedObject = destinationArray.get(destinationArrayIndex);
        if (!clickedObject.isVisited) {
            visited = true;
        } else {
            visited = false;
        }
        clickedObject.isVisited = visited;
        String newData = gson.toJson(destinationArray);
        updatePreferences(newData);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), TripWidgetProvider.class));
        TripWidgetProvider.updateTripWidget(getApplicationContext(), appWidgetManager, appWidgetIds);

        updateTripDatabase(tripDbId, newData);

    }

    private void updatePreferences(String newData) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.trip_data_widget_key), newData);
        editor.apply();
    }

    private void updateTripDatabase(String tripDbId, String updatedData) {
        Uri tripRowUri = FavPlacesContract.TripListsEntry.TRIPS_CONTENT_URI;
        tripRowUri = tripRowUri.buildUpon().appendPath(tripDbId).build();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavPlacesContract.TripListsEntry.TRIP_LIST_JSON, updatedData);

        getApplicationContext().getContentResolver().update(tripRowUri,
                contentValues,
                null,
                null);

    }
}
