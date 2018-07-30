package com.briatka.pavol.favouriteplaces.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.customobjects.CustomPlace;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateTripAdapter extends RecyclerView.Adapter<CreateTripAdapter.ViewHolder> {

    private ArrayList<CustomPlace> mPlaceList;
    private Context mContext;

    public CreateTripAdapter(Context context, ArrayList<CustomPlace> placeList) {
        this.mContext = context;
        this.mPlaceList = placeList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.create_list_location_name)
        TextView locationName;


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
                .inflate(R.layout.create_trip_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final CustomPlace currentObject = mPlaceList.get(position);

        holder.itemView.setTag(position);
        holder.locationName.setText(currentObject.placeName);

    }

    @Override
    public int getItemCount() {
        if (null == mPlaceList) return 0;
        return mPlaceList.size();
    }

    public void setCreateTripData(ArrayList<CustomPlace> passedList) {
        mPlaceList = passedList;
        notifyDataSetChanged();
    }
}
