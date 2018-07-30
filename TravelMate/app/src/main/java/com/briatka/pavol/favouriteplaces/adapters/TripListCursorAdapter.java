package com.briatka.pavol.favouriteplaces.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract;
import com.briatka.pavol.favouriteplaces.customobjects.CustomPlace;
import com.briatka.pavol.favouriteplaces.interfaces.DeleteItemListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripListCursorAdapter extends RecyclerView.Adapter<TripListCursorAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    OnTripListItemClickListener clickListener;
    private DeleteItemListener deleteTripListener;

    public interface OnTripListItemClickListener {
        void onTripListItemClicked(int id);
    }

    public TripListCursorAdapter(Context context, DeleteItemListener deleteTrip) {
        this.mContext = context;
        this.clickListener = (OnTripListItemClickListener) context;
        this.deleteTripListener = deleteTrip;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trip_name)
        TextView tName;
        @BindView(R.id.destinations_visited)
        TextView destinationsVisited;
        @BindView(R.id.cancel_trip_img)
        ImageView cancelTrip;


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
                .inflate(R.layout.main_trip_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        int idIndex = mCursor.getColumnIndex(FavPlacesContract.TripListsEntry._ID);
        int nameIndex = mCursor.getColumnIndex(FavPlacesContract.TripListsEntry.TRIP_NAME);
        int dataIndex = mCursor.getColumnIndex(FavPlacesContract.TripListsEntry.TRIP_LIST_JSON);

        mCursor.moveToPosition(position);

        //get values
        int id = mCursor.getInt(idIndex);
        String tripName = mCursor.getString(nameIndex);
        String tripData = mCursor.getString(dataIndex);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CustomPlace>>() {
        }.getType();

        ArrayList<CustomPlace> dataArray = gson.fromJson(tripData, type);
        int allDestinations = dataArray.size();
        int visitedDestinations = 0;

        for (int i = 0; i < allDestinations; i++) {
            if (dataArray.get(i).isVisited) {
                visitedDestinations += 1;
            }
        }

        String destinationScore = String.valueOf(visitedDestinations) + "/" + String.valueOf(allDestinations);

        //set values
        holder.itemView.setTag(id);
        holder.tName.setText(tripName);
        holder.destinationsVisited.setText(destinationScore);

        holder.tName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tripId = (int) holder.itemView.getTag();
                clickListener.onTripListItemClicked(tripId);
            }
        });

        holder.cancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, mContext.getString(R.string.delete_item_toast_message),
                        Toast.LENGTH_LONG).show();
            }
        });

        holder.cancelTrip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String tag = holder.itemView.getTag().toString();
                deleteTripListener.onDeleteButtonClicked(tag);
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapTripListCursor(Cursor newCursor) {

        if (mCursor == newCursor) {
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = newCursor;

        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}
