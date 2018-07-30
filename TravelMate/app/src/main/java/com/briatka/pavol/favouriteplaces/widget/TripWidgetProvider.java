package com.briatka.pavol.favouriteplaces.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.tripintentservice.TripIntentService;

/**
 * Implementation of App Widget functionality.
 */
public class TripWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views;

        views = getRemoteViews(context);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateTripWidget(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static RemoteViews getRemoteViews(Context context) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.trip_widget_list_view);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tripName = sharedPreferences.getString(context.getString(R.string.trip_name_widget_key),
                context.getString(R.string.widget_default_trip_name));
        String tripId = sharedPreferences.getString(context.getString(R.string.trip_id_widget_key),
                context.getString(R.string.widget_default_trip_id));

        remoteViews.setTextViewText(R.id.widget_trip_name, tripName);

        Intent intent = new Intent(context, TripWidgetService.class);
        remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);


        Intent serviceIntent = new Intent(context, TripIntentService.class);
        serviceIntent.setAction(TripIntentService.ACTION_DESTINATION_VISIT);
        serviceIntent.putExtra(TripIntentService.TRIP_ID_KEY, tripId);
        PendingIntent destinationVisitPendingIntent = PendingIntent.getService(context,
                0,
                serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.widget_list_view, destinationVisitPendingIntent);

        remoteViews.setEmptyView(R.id.widget_list_view, R.id.empty_view);

        return remoteViews;
    }
}

