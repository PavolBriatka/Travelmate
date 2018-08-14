package com.briatka.pavol.favouriteplaces.fragments;


import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.activities.AddNewTripActivity;
import com.briatka.pavol.favouriteplaces.adapters.TripListMainAdapter;
import com.briatka.pavol.favouriteplaces.customobjects.TripObject;
import com.briatka.pavol.favouriteplaces.executors.AppExecutors;
import com.briatka.pavol.favouriteplaces.interfaces.DeleteItemListener;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;
import com.briatka.pavol.favouriteplaces.viewmodels.TripMainViewModel;
import com.briatka.pavol.favouriteplaces.widget.TripWidgetProvider;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripListFragment extends Fragment {

    private TripListMainAdapter adapter;

    private TravelMateDatabase mDatabase;

    private String itemToDeleteName;

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
        public void onDeleteButtonClicked( final int tag) {



            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<TripObject> trips = adapter.getTripList();
                    mDatabase.tripDao().deleteTrip(trips.get(tag));
                    itemToDeleteName = trips.get(tag).getTitle();

                    updatePreferences(itemToDeleteName);

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getContext(), TripWidgetProvider.class));
                    TripWidgetProvider.updateTripWidget(getContext(),appWidgetManager,appWidgetIds);
                }
            });


        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        ButterKnife.bind(this,rootView);

        mDatabase = TravelMateDatabase.getInstance(getContext());

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        adapter = new TripListMainAdapter(getContext(), new DeleteTrip());

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        addNewTripFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAddPlaceActivity = new Intent(getContext(), AddNewTripActivity.class);
                startActivity(openAddPlaceActivity);

            }
        });

        setupViewModel();

        return rootView;
    }

    private void setupViewModel() {
        TripMainViewModel viewModel = ViewModelProviders.of(this).get(TripMainViewModel.class);
        final LiveData<List<TripObject>> trips = viewModel.getTripList();
        trips.observe(this, new Observer<List<TripObject>>() {
            @Override
            public void onChanged(@Nullable List<TripObject> tripObjects) {
                adapter.setTrips(tripObjects);
                if (tripObjects == null || tripObjects.size() == 0) {
                    emptyTripList.setVisibility(View.VISIBLE);
                } else {
                    emptyTripList.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updatePreferences(String deletedItem){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String storedName = sharedPreferences.getString(getString(R.string.trip_name_widget_key),
                getString(R.string.widget_default_trip_name));
        if(storedName.equals(deletedItem)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.trip_data_widget_key), getString(R.string.widget_default_list_item_values));
            editor.putString(getString(R.string.trip_name_widget_key), getString(R.string.widget_default_trip_name));
            editor.apply();
        }
    }
}
