package com.briatka.pavol.favouriteplaces.fragments;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
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
import com.briatka.pavol.favouriteplaces.activities.AddNewFavPlaceActivity;
import com.briatka.pavol.favouriteplaces.adapters.FavPlacesAdapter;
import com.briatka.pavol.favouriteplaces.customobjects.FavPlaceObject;
import com.briatka.pavol.favouriteplaces.executors.AppExecutors;
import com.briatka.pavol.favouriteplaces.interfaces.DeleteItemListener;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;
import com.briatka.pavol.favouriteplaces.viewmodels.PlaceMainViewModel;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavPlacesFragment extends Fragment {

    private FavPlacesAdapter adapter;

    private TravelMateDatabase mDatabase;


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
        public void onDeleteButtonClicked(final int tag) {

            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<FavPlaceObject> places = adapter.getPlacesArray();
                    mDatabase.favPlaceDao().deletePlace(places.get(tag));
                }
            });

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fav_places, container, false);
        ButterKnife.bind(this, rootView);

        mDatabase = TravelMateDatabase.getInstance(getContext());

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        adapter = new FavPlacesAdapter(getContext(), new DeletePlace());

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        addNewPlaceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openAddPlaceActivity = new Intent(getContext(), AddNewFavPlaceActivity.class);
                startActivity(openAddPlaceActivity);
            }
        });

        setupViewModel();

        return rootView;
    }



    private void setupViewModel() {
        PlaceMainViewModel viewModel = ViewModelProviders.of(this).get(PlaceMainViewModel.class);
        final LiveData<List<FavPlaceObject>> placeList = viewModel.getPlaceList();
        placeList.observe(this, new Observer<List<FavPlaceObject>>() {
            @Override
            public void onChanged(@Nullable List<FavPlaceObject> favPlaceObjects) {
                adapter.setPlaces(favPlaceObjects);
                if (placeList == null || favPlaceObjects.size() == 0) {
                    emptyPlaceList.setVisibility(View.VISIBLE);
                } else {
                    emptyPlaceList.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
