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
import com.briatka.pavol.favouriteplaces.customobjects.FavPlaceObject;
import com.briatka.pavol.favouriteplaces.interfaces.DeleteItemListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavPlacesAdapter extends RecyclerView.Adapter<FavPlacesAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    private List<FavPlaceObject> mPlacesArray;

    private OnFavPlaceItemClickListener clickListener;
    private DeleteItemListener deletePlaceListener;

    public interface OnFavPlaceItemClickListener {
        void onFavPlaceItemClicked(int id);
    }

    public FavPlacesAdapter(Context context, DeleteItemListener deletePlace) {
        this.mContext = context;
        this.clickListener = (OnFavPlaceItemClickListener) context;
        this.deletePlaceListener = deletePlace;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.place_title)
        TextView pTitle;
        @BindView(R.id.delete_place_img)
        ImageView deletePlace;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }


    @NonNull
    @Override
    public FavPlacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.favplaces_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FavPlacesAdapter.ViewHolder holder, int position) {


        FavPlaceObject currentObject = mPlacesArray.get(position);
        String name = currentObject.getTitle();
        int id = currentObject.getId();

        holder.itemView.setTag(id);
        holder.pTitle.setText(name);

        holder.pTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int placeId = (int) holder.itemView.getTag();
                clickListener.onFavPlaceItemClicked(placeId);
            }
        });

        holder.deletePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, mContext.getString(R.string.delete_item_toast_message),
                        Toast.LENGTH_LONG).show();
            }
        });

        holder.deletePlace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deletePlaceListener.onDeleteButtonClicked(holder.getAdapterPosition());
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mPlacesArray == null) {
            return 0;
        }
        return mPlacesArray.size();
    }


    public void setPlaces(List<FavPlaceObject> placesArray){
        mPlacesArray = placesArray;
        notifyDataSetChanged();
    }

    public List<FavPlaceObject> getPlacesArray(){
        return mPlacesArray;
    }
}
