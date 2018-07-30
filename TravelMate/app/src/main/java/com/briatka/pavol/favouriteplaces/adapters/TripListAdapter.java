package com.briatka.pavol.favouriteplaces.adapters;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract;
import com.briatka.pavol.favouriteplaces.customobjects.CustomPlace;
import com.briatka.pavol.favouriteplaces.widget.TripWidgetProvider;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder> {

    private ArrayList<CustomPlace> mPlaceList;
    private Context mContext;
    private String mTripId;


    public TripListAdapter(Context context, ArrayList<CustomPlace> placeList) {
        this.mContext = context;
        this.mPlaceList = placeList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_name)
        TextView lName;
        @BindView(R.id.trip_list_check_box)
        ImageView destinationCheckBox;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.trip_detail_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final CustomPlace currentObject = mPlaceList.get(position);

        if (currentObject.isVisited) {
            holder.destinationCheckBox.setImageResource(R.drawable.outline_check_box_black_24);
        } else {
            holder.destinationCheckBox.setImageResource(R.drawable.outline_check_box_outline_blank_black_24);
        }

        holder.itemView.setTag(position);

        holder.lName.setText(currentObject.placeName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean visited;
                if (currentObject.isVisited) {
                    holder.destinationCheckBox.setImageResource(R.drawable.outline_check_box_outline_blank_black_24);
                    visited = false;
                } else {
                    holder.destinationCheckBox.setImageResource(R.drawable.outline_check_box_black_24);
                    visited = true;
                }
                currentObject.isVisited = visited;
                onClickUpdate(mPlaceList);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (null == mPlaceList) return 0;
        return mPlaceList.size();
    }

    public void setTripListData(ArrayList<CustomPlace> passedList) {
        this.mPlaceList = passedList;
        Log.e("size2", String.valueOf(mPlaceList.size()));
        notifyDataSetChanged();
    }


    public void setTripId(String passedTripId) {
        mTripId = passedTripId;
    }

    private void onClickUpdate(ArrayList<CustomPlace> updatedList) {
        Gson gson = new Gson();
        String newData = gson.toJson(updatedList);

        updatePreferences(newData);
        updateTripDatabase(newData);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, TripWidgetProvider.class));
        TripWidgetProvider.updateTripWidget(mContext, appWidgetManager, appWidgetIds);
    }

    private void updatePreferences(String newData) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(mContext.getString(R.string.trip_data_widget_key), newData);
        editor.apply();
    }

    private void updateTripDatabase(String updatedData) {
        Uri tripRowUri = FavPlacesContract.TripListsEntry.TRIPS_CONTENT_URI;
        tripRowUri = tripRowUri.buildUpon().appendPath(mTripId).build();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavPlacesContract.TripListsEntry.TRIP_LIST_JSON, updatedData);

        mContext.getContentResolver().update(tripRowUri,
                contentValues,
                null,
                null);

    }
}
