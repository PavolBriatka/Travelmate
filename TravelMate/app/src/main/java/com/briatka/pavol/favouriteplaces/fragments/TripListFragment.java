package com.briatka.pavol.favouriteplaces.fragments;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.briatka.pavol.favouriteplaces.activities.AddNewTripActivity;
import com.briatka.pavol.favouriteplaces.adapters.TripListCursorAdapter;
import com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract;
import com.briatka.pavol.favouriteplaces.interfaces.DeleteItemListener;
import com.briatka.pavol.favouriteplaces.widget.TripWidgetProvider;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private TripListCursorAdapter adapter;
    final LoaderManager.LoaderCallbacks callbacks = this;

    @BindView(R.id.trip_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fab_add_new_trip)
    FloatingActionButton addNewTripFab;
    @BindView(R.id.trip_list_empty_state)
    TextView emptyTripList;
    @BindView(R.id.spin_kit_trip_fragment)
    SpinKitView progressBar;




    public TripListFragment() {
        // Required empty public constructor
    }

    public class DeleteTrip implements DeleteItemListener {

        @Override
        public void onDeleteButtonClicked(String tag) {

            Uri deleteTripPath = FavPlacesContract.TripListsEntry.TRIPS_CONTENT_URI;
            deleteTripPath = deleteTripPath.buildUpon().appendPath(tag).build();

            getContext().getContentResolver().delete(deleteTripPath,null,null);

            getLoaderManager().restartLoader(LOADER_ID,null,callbacks);

            updatePreferences();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getContext(), TripWidgetProvider.class));
            TripWidgetProvider.updateTripWidget(getContext(),appWidgetManager,appWidgetIds);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        ButterKnife.bind(this,rootView);

        getLoaderManager().initLoader(LOADER_ID, null,this);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        adapter = new TripListCursorAdapter(getContext(), new DeleteTrip());

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        addNewTripFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAddPlaceActivity = new Intent(getContext(), AddNewTripActivity.class);
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
                    Uri uri = FavPlacesContract.TripListsEntry.TRIPS_CONTENT_URI;

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
            emptyTripList.setVisibility(View.VISIBLE);
        } else {
            emptyTripList.setVisibility(View.GONE);
        }


        adapter.swapTripListCursor(data);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapTripListCursor(null);
    }

    private void updatePreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.trip_data_widget_key), getString(R.string.widget_default_list_item_values));
        editor.putString(getString(R.string.trip_name_widget_key),getString(R.string.widget_default_trip_name));
        editor.apply();
    }
}
