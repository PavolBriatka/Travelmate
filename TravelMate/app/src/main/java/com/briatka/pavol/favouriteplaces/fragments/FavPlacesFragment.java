package com.briatka.pavol.favouriteplaces.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.activities.AddNewFavPlaceActivity;
import com.briatka.pavol.favouriteplaces.adapters.FavPlacesCursorAdapter;
import com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract;
import com.briatka.pavol.favouriteplaces.interfaces.DeleteItemListener;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavPlacesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;
    private FavPlacesCursorAdapter adapter;
    final LoaderManager.LoaderCallbacks callbacks = this;

    @BindView(R.id.fav_places_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fab_add_new_place)
    FloatingActionButton addNewPlaceFab;
    @BindView(R.id.place_list_empty_state)
    TextView emptyPlaceList;
    @BindView(R.id.spin_kit_places_fragment)
    SpinKitView progressBar;


    public FavPlacesFragment() {
        // Required empty public constructor
    }

    public class DeletePlace implements DeleteItemListener {

        @Override
        public void onDeleteButtonClicked(String tag) {

            Uri deletePlacePath = FavPlacesContract.FavPlacesEntry.PLACES_CONTENT_URI;
            deletePlacePath = deletePlacePath.buildUpon().appendPath(tag).build();

            getContext().getContentResolver().delete(deletePlacePath, null, null);

            getLoaderManager().restartLoader(LOADER_ID, null, callbacks);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fav_places, container, false);
        ButterKnife.bind(this, rootView);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        adapter = new FavPlacesCursorAdapter(getContext(), new DeletePlace());

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        addNewPlaceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAddPlaceActivity = new Intent(getContext(), AddNewFavPlaceActivity.class);
                startActivity(openAddPlaceActivity);

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {

            Cursor receivedData = null;

            @Override
            protected void onStartLoading() {
                if (receivedData != null) {
                    deliverResult(receivedData);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    Context appContext = getActivity().getApplicationContext();
                    Uri uri = FavPlacesContract.FavPlacesEntry.PLACES_CONTENT_URI;

                    return appContext.getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception exception) {
                    exception.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                receivedData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            emptyPlaceList.setVisibility(View.VISIBLE);
        } else {
            emptyPlaceList.setVisibility(View.GONE);
        }
        adapter.swapCursor(data);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }

}
