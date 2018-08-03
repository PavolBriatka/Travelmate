package com.briatka.pavol.favouriteplaces.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.customobjects.CustomPlace;
import com.briatka.pavol.favouriteplaces.tripintentservice.TripIntentService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TripWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TripListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class TripListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<CustomPlace> tripDataList = new ArrayList<>();
    private String tripData;

    public TripListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        tripDataList.clear();


        Type type = new TypeToken<ArrayList<CustomPlace>>() {
        }.getType();
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        tripData = sharedPreferences.getString(
                mContext.getString(R.string.trip_data_widget_key),
                mContext.getString(R.string.widget_default_list_item_values));
        if (tripData.contains("[")) {
            tripDataList = gson.fromJson(tripData, type);
        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tripDataList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.trip_widget);
        CustomPlace currentObject = tripDataList.get(position);
        if (!currentObject.isVisited) {
            views.setImageViewResource(R.id.destination_line_check_box,
                    R.drawable.outline_check_box_outline_blank_black_24);
        } else {
            views.setImageViewResource(R.id.destination_line_check_box,
                    R.drawable.outline_check_box_black_24);
        }
        views.setTextViewText(R.id.destination_name_line, currentObject.placeName);

        Bundle extras = new Bundle();
        extras.putInt(TripIntentService.DESTINATION_POSITION_KEY, position);
        extras.putString(TripIntentService.TRIP_DATA_KEY, tripData);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.list_item_relative_layout, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
