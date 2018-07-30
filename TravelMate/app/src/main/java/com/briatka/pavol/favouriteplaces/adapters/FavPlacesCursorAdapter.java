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
import com.briatka.pavol.favouriteplaces.interfaces.DeleteItemListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavPlacesCursorAdapter extends RecyclerView.Adapter<FavPlacesCursorAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    private OnFavPlaceItemClickListener clickListener;
    private DeleteItemListener deletePlaceListener;

    public interface OnFavPlaceItemClickListener {
        void onFavPlaceItemClicked(int id);
    }

    public FavPlacesCursorAdapter(Context context, DeleteItemListener deletePlace) {
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
    public FavPlacesCursorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.favplaces_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FavPlacesCursorAdapter.ViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(FavPlacesContract.FavPlacesEntry._ID);
        int nameIndex = mCursor.getColumnIndex(FavPlacesContract.FavPlacesEntry.PLACE_NAME);

        mCursor.moveToPosition(position);

        int id = mCursor.getInt(idIndex);
        String name = mCursor.getString(nameIndex);

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
                String tag = holder.itemView.getTag().toString();
                deletePlaceListener.onDeleteButtonClicked(tag);
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

    public Cursor swapCursor(Cursor newCursor) {

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
